package uz.uzkassa.cts.service.impl;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.dto.orderspecification.request.OrderSpecificationChangeStatusDto;
import uz.uzkassa.cts.dto.orderspecification.request.OrderSpecificationRequestDto;
import uz.uzkassa.cts.dto.orderspecification.response.OrderSpecificationResponseDto;
import uz.uzkassa.cts.dto.orderspecification.response.ServiceOrderSpecificationListDto;
import uz.uzkassa.cts.dto.uploadfile.response.UploadFileResponse;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.mappings.ServiceOrderSpecificationMapper;
import uz.uzkassa.cts.repository.CtsServiceOrderRepository;
import uz.uzkassa.cts.repository.CtsServiceOrderSpecificationRepository;
import uz.uzkassa.cts.repository.CustomerBranchDeviceRepository;
import uz.uzkassa.cts.repository.FiscalizationRepository;
import uz.uzkassa.cts.service.*;
import uz.uzkassa.cts.utils.BigDecimalUtil;
import uz.uzkassa.cts.utils.DateUtil;
import uz.uzkassa.cts.utils.NumberToWordUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Optional;

@Transactional
@Slf4j
@Service
public class ServiceOrderSpecificationOldServiceImpl extends BaseServiceImpl<CtsServiceOrderSpecification, Long> implements ServiceOrderSpecificationOldService {

    private final CtsServiceOrderRepository orderRepository;
    private final VatService vatService;
    private final ThymeleafService thymeleafService;
    private final FileStorageService fileStorageService;
    private final CustomerBranchDeviceRepository customerBranchDeviceRepository;
    private final FileUploadService fileUploadService;
    private final CtsServiceOrderSpecificationRepository repository;
    private final FiscalizationRepository fiscalizationRepository;
    private final ServiceOrderSpecificationMapper serviceOrderSpecificationMapper;

    public ServiceOrderSpecificationOldServiceImpl(CtsServiceOrderSpecificationRepository repository,
                                                   CtsServiceOrderRepository orderRepository,
                                                   VatService vatService, ThymeleafService thymeleafService,
                                                   FileStorageService fileStorageService,
                                                   CustomerBranchDeviceRepository customerBranchDeviceRepository,
                                                   FileUploadService fileUploadService,
                                                   FiscalizationRepository fiscalizationRepository,
                                                   ServiceOrderSpecificationMapper serviceOrderSpecificationMapper) {
        super(repository);
        this.repository = repository;
        this.orderRepository = orderRepository;
        this.vatService = vatService;
        this.thymeleafService = thymeleafService;
        this.fileStorageService = fileStorageService;
        this.customerBranchDeviceRepository = customerBranchDeviceRepository;
        this.fileUploadService = fileUploadService;
        this.fiscalizationRepository = fiscalizationRepository;
        this.serviceOrderSpecificationMapper = serviceOrderSpecificationMapper;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(isolation = Isolation.SERIALIZABLE)
    public OrderSpecificationResponseDto create(OrderSpecificationRequestDto dto) {
        CtsServiceOrder order = orderRepository.findById(dto.getOrderId()).orElseThrow(validationService.entityNotFoundExceptionThrow(ORDER));

        Customer customer = order.getCustomer();
        if (CustomerAppType.CTS != customer.getAppType()) {
            throw validationService.requiredExceptionThrow(CustomerAppType.CTS.name()).get();
        }

        Contract contract = order.getContract();

        CtsServiceOrderSpecification specification = order.getServiceOrderSpecification();
        if (specification != null) {
            throw validationService.entityAlreadyExistExceptionThrow(SPECIFICATION, "number", specification.getNumber()).get();
        }

        specification = new CtsServiceOrderSpecification();
        specification.setContractId(contract.getId());
        specification.setOrderId(order.getId());

        Vat vat = vatService.getActiveVat();

        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal totalSellPrice = BigDecimal.ZERO;
        BigDecimal totalVatPrice;
        int totalProductCount = 0;

        for (OrderTariff orderTariff : order.getOrderTariffs()) {
            Tariff tariff = orderTariff.getTariff();

            CtsServiceOrderSpecificationDetail details = new CtsServiceOrderSpecificationDetail();
            details.setTariffId(tariff.getId());
            details.setTariffName(tariff.getName());

            int deviceCount = orderTariff.getDeviceTariffs().size();
            BigDecimal vatPriceFor1 = BigDecimalUtil.calculateVatPriceFor1(tariff.getPrice(), vat.getPercent());
            BigDecimal productPriceFor1 = BigDecimalUtil.calculateProductPriceFor1SubtractVatPrice(tariff.getPrice(), vatPriceFor1);


            details.setProductCount(deviceCount);
            details.setPrice1pc(productPriceFor1);
            details.setSellPrice1pc(tariff.getPrice());
            details.setVatPrice1pc(vatPriceFor1);
            details.setVatRate(vat.getPercent());

            BigDecimal overallPrice = productPriceFor1.multiply(BigDecimal.valueOf(deviceCount));
            BigDecimal overallSellPrice = tariff.getPrice().multiply(BigDecimal.valueOf(deviceCount));
            BigDecimal overallVatPrice = vatPriceFor1.multiply(BigDecimal.valueOf(deviceCount));
            details.setOverallPrice(overallPrice);
            details.setOverallSellPrice(overallSellPrice);
            details.setOverallVatPrice(overallVatPrice);

            specification.addChild(details);
            specification.setBonus(tariff.getBonus());


            totalSellPrice = totalSellPrice.add(overallSellPrice);
            totalProductCount = totalProductCount + deviceCount;

            for (DeviceTariff deviceTariff : orderTariff.getDeviceTariffs()) {
                CustomerBranchDevice customerBranchDevice = deviceTariff.getCustomerBranchDevice();
                // if fiscalization is not null it means changing tariff
                Fiscalization fiscalization = fiscalizationRepository.findFirstByDeviceIdOrderByIdDesc(customerBranchDevice.getDeviceId()).orElse(null);
                if (fiscalization != null) {
                    // if this fiscal but in trail period change only tariff
                    // if no in trail period change startDate and dueDate
                    if (!customerBranchDevice.isFreeTrailPeriod()) {

                        LocalDate startDate = customerBranchDevice.getDueDate();
                        if (DeviceSuspendStatus.DEREGISTER.equals(customerBranchDevice.getSuspendStatus())) {
                            startDate = LocalDate.now();
                            customerBranchDevice.setSuspendStatus(DeviceSuspendStatus.ACTIVE);
                            customerBranchDevice.setSuspendDate(startDate);
                            customerBranchDevice.setStartDate(startDate);
                        }
                        customerBranchDevice.setStartDate(startDate);
                        if (PeriodType.YEARLY.equals(tariff.getPeriodType()))
                            customerBranchDevice.setDueDate(startDate);
                        else
                            customerBranchDevice.setDueDate(DateUtil.calculateDueDate(startDate));
                    }
                }
                // Set service allowed false -- to show this KKM on fiscalization
                customerBranchDevice.setServiceAllowed(false);
                customerBranchDevice.setTariffId(tariff.getId());
                customerBranchDevice.setContractId(contract.getId());
                customerBranchDeviceRepository.save(customerBranchDevice);
            }
        }

        BigDecimal totalVat = BigDecimalUtil.calculateVatPriceFor1(totalSellPrice, vat.getPercent());
        BigDecimal totalProductPrice = BigDecimalUtil.calculateProductPriceFor1SubtractVatPrice(totalSellPrice, totalVat);
        totalPrice = totalPrice.add(totalProductPrice);
        totalVatPrice = totalVat;

        specification.setTotalPrice(BigDecimalUtil.setScaleByTwo(totalPrice));
        specification.setTotalPriceInWord(NumberToWordUtil.digits2Text(BigDecimalUtil.setScaleByTwo(totalPrice)));
        specification.setTotalSellPrice(BigDecimalUtil.setScaleByTwo(totalSellPrice));
        specification.setTotalSellPriceInWord(NumberToWordUtil.digits2Text(BigDecimalUtil.setScaleByTwo(totalSellPrice)));
        specification.setTotalVatPrice(BigDecimalUtil.setScaleByTwo(totalVatPrice));
        specification.setTotalVatPriceInWord(NumberToWordUtil.digits2Text(BigDecimalUtil.setScaleByTwo(totalVatPrice)));
        specification.setTotalProductCount(totalProductCount);
        specification.setStatus(ContractStatus.WAITING_FOR_DOCUMENT_ATTACHMENT);

        specification = save(specification);
        log.info("Specification created");
        // update order
        order.setOrderStatus(OrderStatus.WAITING_FOR_DOCUMENT_ATTACHMENT);
        orderRepository.save(order);
        log.info("Order status updated to: {}", OrderStatus.WAITING_FOR_DOCUMENT_ATTACHMENT);

        return serviceOrderSpecificationMapper.toDto(specification);
    }


    @Override
    public String getSpecificationHtmlTemplate(Long id) {
        CtsServiceOrderSpecification specification = findById(id);
        Optional<String> stringOptional = orderRepository.findDevices(specification.getOrderId());

//        NumberToWordUtil.digits2Text(BigDecimal.valueOf(50000.12));
        return thymeleafService.generateSpecificationForServiceHTML(stringOptional.orElse(""), specification);
    }

    @Override
    public OrderSpecificationResponseDto uploadFile(Long id, MultipartFile file) {
        CtsServiceOrderSpecification specification = findById(id);
        UploadFileResponse response = fileUploadService.upload(file, id, EntityTypeEnum.SERVICE_SPECIFICATION.name(), DocumentType.SERVICE_SPECIFICATION.name());

        specification.setSpecFileName(response.getFileName());
        specification.setSpecContentType(file.getContentType());
        specification.setSpecFileUri(response.getFileDownloadUri());
        if (specification.getStatus() != ContractStatus.ACCEPTED)
            specification.setStatus(ContractStatus.DOCUMENT_ATTACHED);
        // update spec with file
        specification = save(specification);

        CtsServiceOrder order = specification.getOrder();
        order.setOrderStatus(OrderStatus.CLOSED);
        orderRepository.save(order);

        return serviceOrderSpecificationMapper.toDto(specification);
    }

    @Override
    public Resource downloadFile(Long id) {
        CtsServiceOrderSpecification specification = findById(id);
        Customer customer = specification.getContract().getCustomer();

        return fileStorageService.loadFileAsResource(customer.getTin(), specification.getSpecFileName());
    }

    @Override
    public OrderSpecificationResponseDto changeStatus(Long id, OrderSpecificationChangeStatusDto dto) {
        CtsServiceOrderSpecification specification = findById(id);
        Customer customer = specification.getContract().getCustomer();
        if (!customer.getId().equals(dto.getCustomerId()))
            throw validationService.entityNotFoundExceptionThrow(SPECIFICATION).get();

        specification.setStatus(dto.getContractStatus());
        specification = save(specification);

        CtsServiceOrder order = specification.getOrder();
        if (specification.getStatus() == ContractStatus.ACCEPTED) {
            order.setOrderStatus(OrderStatus.WAITING_FOR_PAYMENT);
            orderRepository.save(order);
        }

        return serviceOrderSpecificationMapper.toDto(specification);
    }

    @Override
    public OrderSpecificationRequestDto update(OrderSpecificationRequestDto dto) {
        return null;
    }

    @Override
    public OrderSpecificationResponseDto getById(Long id) {
        CtsServiceOrderSpecification orderSpecification = findById(id);
        return serviceOrderSpecificationMapper.toDto(orderSpecification);
    }

    @Override
    public Page<OrderSpecificationResponseDto> getByContractId(Long contractId, BaseFilter filter) {

        Page<CtsServiceOrderSpecification> specifications = repository.findByContractIdOrderByIdDesc(contractId, filter.getOrderedPageable());

        return specifications.map(serviceOrderSpecificationMapper::toDto);
    }

    @Override
    public Page<ServiceOrderSpecificationListDto> list(BaseFilter filter) {
        Specification<CtsServiceOrderSpecification> specification = (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);

            Path<ContractStatus> orderStatus = root.get("status");
            Path<LocalDateTime> createDate = root.get("createdDate");
            Path<Long> idPath = root.get("id");
            Join<CtsServiceOrderSpecification, Contract> contractJoin = root.join("contract", JoinType.INNER);
            Path<Customer> customer = contractJoin.join("customer", JoinType.INNER);
            Path<String> tin = customer.get("tin");
            Path<String> customerName = customer.get("name");
            Path<String> customerCompanyName = customer.get("companyName");
            Path<String> contractNumberPath = contractJoin.get("name");

            ArrayList<Predicate> list = new ArrayList<>();
            if (StringUtils.isNotEmpty(filter.getSearch())) {
                String search = filter.getSearch().toUpperCase();
                Predicate customerNamePred = criteriaBuilder.like(criteriaBuilder.upper(customerName), "%" + search + "%");
                Predicate customerCompanyNamePred = criteriaBuilder.like(criteriaBuilder.upper(customerCompanyName), "%" + search + "%");
                Predicate tinPred = criteriaBuilder.like(criteriaBuilder.upper(tin), "%" + search + "%");
                Predicate idPred = criteriaBuilder.equal(idPath, search);
                Predicate contractNumberPred = criteriaBuilder.like(contractNumberPath, search);
                list.add(criteriaBuilder.or(customerNamePred, tinPred, customerCompanyNamePred, idPred, contractNumberPred));
            }
            if (filter.getStatus() != null)
                list.add(criteriaBuilder.equal(orderStatus, ContractStatus.valueOf(filter.getStatus())));

            if (filter.getFrom() != null && filter.getTo() != null)
                list.add(criteriaBuilder.between(createDate, filter.getFrom().atStartOfDay(), filter.getTo().atTime(LocalTime.MAX)));

            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
        Page<CtsServiceOrderSpecification> orders = repository.findAll(specification, filter.getOrderedPageable());

        return orders.map(serviceOrderSpecificationMapper::toListDto);
    }
}
