package uz.uzkassa.cts.service.impl;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.domain.address.BaseAddress;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.onetimeorder.request.OneTimeOrderChangeStatusRequestDto;
import uz.uzkassa.cts.dto.onetimeorder.request.OneTimeRequestCreatDto;
import uz.uzkassa.cts.dto.onetimeorder.request.OneTimeRequestDto;
import uz.uzkassa.cts.dto.onetimeorder.response.OneTimeOrderResponseDto;
import uz.uzkassa.cts.dto.serviceorder.ServiceOrderListDTO;
import uz.uzkassa.cts.dto.uploadfile.response.UploadFileResponse;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.EmployeeReportFilter;
import uz.uzkassa.cts.filter.OneTimeOrderFilter;
import uz.uzkassa.cts.mappings.OneTimeOrderMapper;
import uz.uzkassa.cts.repository.FiscalizationRepository;
import uz.uzkassa.cts.repository.OneTimeOrderRepository;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.*;
import uz.uzkassa.cts.utils.BigDecimalUtil;
import uz.uzkassa.cts.utils.InvoiceCreateExcelUtil;
import uz.uzkassa.cts.utils.NumberToWordUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class OneTimeOrderOldServiceImpl extends BaseServiceImpl<OneTimeOrder, Long> implements OneTimeOrderOldService {

    private final OneTimeOrderRepository repository;
    private final CustomerOldService customerOldService;
    private final CtsServiceService ctsServiceService;
    private final ContractService contractService;
    private final VatService vatService;
    private final FileUploadService fileUploadService;
    private final ThymeleafService thymeleafService;
    private final FiscalizationRepository fiscalizationRepository;
    private final OneTimeOrderMapper oneTimeOrderMapper;

    public OneTimeOrderOldServiceImpl(OneTimeOrderRepository repository,
                                      CustomerOldService customerOldService,
                                      CtsServiceService ctsServiceService,
                                      ContractService contractService,
                                      VatService vatService, FileUploadService fileUpload,
                                      ThymeleafService thymeleafService,
                                      FiscalizationRepository fiscalizationRepository,
                                      OneTimeOrderMapper oneTimeOrderMapper) {
        super(repository);
        this.repository = repository;
        this.customerOldService = customerOldService;
        this.ctsServiceService = ctsServiceService;
        this.contractService = contractService;
        this.vatService = vatService;
        this.fileUploadService = fileUpload;
        this.thymeleafService = thymeleafService;
        this.fiscalizationRepository = fiscalizationRepository;
        this.oneTimeOrderMapper = oneTimeOrderMapper;
    }

    @Override
    public Page<ServiceOrderListDTO> listByEmployee(EmployeeReportFilter filter) {
        ResultList<ServiceOrderListDTO> resultList = repository.getListByEmployee(filter);
        BigDecimal totalBonus = repository.totalBonusListByEmployee(filter);
        List<ServiceOrderListDTO> result =
                resultList.getList().stream().peek(serviceOrderListDTO -> serviceOrderListDTO.setTotalBonus(totalBonus)).collect(Collectors.toList());

        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public Page<OneTimeOrderResponseDto> list(OneTimeOrderFilter filter) {
        Specification<OneTimeOrder> specification = (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);

            Join<OneTimeOrder, Customer> customer = root.join("customer", JoinType.INNER);
            Join<Customer, Branch> branchJoin = customer.join(Customer_.BRANCHES, JoinType.INNER);
            Path<BaseAddress> addressPath = customer.get("address");

            ArrayList<Predicate> list = new ArrayList<>();
            if (!SecurityUtils.isCurrentUserInRoles(Role.ADMIN.getRoleCode())) {
                Path<Long> branchId = branchJoin.get("id");
                list.add(criteriaBuilder.equal(branchId, SecurityUtils.getBranchId()));
            }
            if (StringUtils.isNotEmpty(filter.getSearch())) {
                Path<String> tin = customer.get("tin");
                Path<String> customerName = customer.get("name");
                Path<String> customerCompanyName = customer.get("companyName");

                Predicate customerNamePred = criteriaBuilder.like(criteriaBuilder.lower(customerName), filter.getSearchForQuery());
                Predicate customerCompanyNamePred = criteriaBuilder.like(criteriaBuilder.lower(customerCompanyName), filter.getSearchForQuery());
                Predicate tinPred = criteriaBuilder.like(criteriaBuilder.lower(tin), filter.getSearchForQuery());
                Predicate serialNumberKKMPred = criteriaBuilder.like(criteriaBuilder.lower(root.get(OneTimeOrder_.SERIAL_NUMBER_KK_M)), filter.getSearchForQuery());
                list.add(criteriaBuilder.or(customerNamePred, tinPred, customerCompanyNamePred, serialNumberKKMPred));
            }
            if (CollectionUtils.isNotEmpty(filter.getStatuses())) {
                list.add(criteriaBuilder.in(root.get(OneTimeOrder_.STATUS)).value(filter.getStatuses()));
            }
            if (filter.getCreatorId() != null) {
                Path<Long> creatorIdPath = root.get("creatorId");
                list.add(criteriaBuilder.equal(creatorIdPath, filter.getCreatorId()));
            }
            if (filter.getRegionId() != null) {
                Path<Long> regionIdPath = addressPath.get("regionId");
                list.add(criteriaBuilder.equal(regionIdPath, filter.getRegionId()));
            }
            if (filter.getDistrictId() != null) {
                Path<Long> districtIdPath = addressPath.get("districtId");
                list.add(criteriaBuilder.equal(districtIdPath, filter.getDistrictId()));
            }
            if (CollectionUtils.isNotEmpty(filter.getServiceIds())) {
                list.add(criteriaBuilder.in(root.get(OneTimeOrder_.SERVICE_ID)).value(filter.getServiceIds()));
            }
            if (filter.getFrom() != null && filter.getTo() != null) {
                Path<LocalDateTime> createDate = root.get("createdDate");
                list.add(criteriaBuilder.between(createDate, filter.getFrom().atStartOfDay(), filter.getTo().atTime(LocalTime.MAX)));
            }
            if (filter.getModifiedFrom() != null && filter.getModifiedTo() != null) {
                Path<LocalDateTime> createDate = root.get(OneTimeOrder_.LAST_MODIFIED_DATE);
                list.add(criteriaBuilder.between(createDate, filter.getModifiedFrom().atStartOfDay(), filter.getModifiedTo().atTime(LocalTime.MAX)));
            }

            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
        Page<OneTimeOrder> orders = repository.findAll(specification, filter.getOrderedPageable());

        return orders.map(oneTimeOrderMapper::toDto);
    }

    @Override
    public byte[] download(OneTimeOrderFilter filter) {
        Specification<OneTimeOrder> specification = (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);

            Join<OneTimeOrder, Customer> customer = root.join(OneTimeOrder_.CUSTOMER, JoinType.INNER);
            Join<Customer, Branch> branchJoin = customer.join(Customer_.BRANCHES, JoinType.INNER);
            Path<BaseAddress> addressPath = customer.get(Customer_.ADDRESS);

            ArrayList<Predicate> list = new ArrayList<>();
            if (!SecurityUtils.isCurrentUserInRoles(Role.ADMIN.getRoleCode())) {
                Path<Long> branchId = branchJoin.get(Branch_.ID);
                list.add(criteriaBuilder.equal(branchId, SecurityUtils.getBranchId()));
            }
            if (StringUtils.isNotEmpty(filter.getSearch())) {
                Path<String> tin = customer.get(Customer_.TIN);
                Path<String> customerName = customer.get(Customer_.NAME);
                Path<String> customerCompanyName = customer.get(Customer_.COMPANY_NAME);

                Predicate customerNamePred = criteriaBuilder.like(criteriaBuilder.lower(customerName), filter.getSearchForQuery());
                Predicate customerCompanyNamePred = criteriaBuilder.like(criteriaBuilder.lower(customerCompanyName), filter.getSearchForQuery());
                Predicate tinPred = criteriaBuilder.like(criteriaBuilder.lower(tin), filter.getSearchForQuery());
                list.add(criteriaBuilder.or(customerNamePred, tinPred, customerCompanyNamePred));
            }
            if (CollectionUtils.isNotEmpty(filter.getStatuses())) {
                list.add(criteriaBuilder.in(root.get(OneTimeOrder_.STATUS)).value(filter.getStatuses()));
            }
            if (filter.getCreatorId() != null) {
                Path<Long> creatorIdPath = root.get(OneTimeOrder_.CREATOR_ID);
                list.add(criteriaBuilder.equal(creatorIdPath, filter.getCreatorId()));
            }
            /*if (filter.getRegionId() != null) {
                Path<Long> regionIdPath = addressPath.get(Address_.REGION_ID);
                list.add(criteriaBuilder.equal(regionIdPath, filter.getRegionId()));
            }
            if (filter.getDistrictId() != null) {
                Path<Long> districtIdPath = addressPath.get(Address_.DISTRICT_ID);
                list.add(criteriaBuilder.equal(districtIdPath, filter.getDistrictId()));
            }*/
            if (CollectionUtils.isNotEmpty(filter.getServiceIds())) {
                list.add(criteriaBuilder.in(root.get(OneTimeOrder_.SERVICE_ID)).value(filter.getServiceIds()));
            }
            if (filter.getFrom() != null && filter.getTo() != null) {
                Path<LocalDateTime> createDate = root.get(OneTimeOrder_.CREATED_DATE);
                list.add(criteriaBuilder.between(createDate, filter.getFrom().atStartOfDay(), filter.getTo().atTime(LocalTime.MAX)));
            }
            if (filter.getModifiedFrom() != null && filter.getModifiedTo() != null) {
                Path<LocalDateTime> createDate = root.get(OneTimeOrder_.LAST_MODIFIED_DATE);
                list.add(criteriaBuilder.between(createDate, filter.getModifiedFrom().atStartOfDay(), filter.getModifiedTo().atTime(LocalTime.MAX)));
            }

            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
        List<OneTimeOrder> details = repository.findAll(specification);

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("OneTimeService");
            String[] headerNames = new String[]{"ИНН", "Название компании", "Регион", "Адрес", "Номер телефона",
                    "МФО", "Расчётный счёт", "Номер счета на оплату", "Дата счета на оплату", "Сумма", "Статус"};
            InvoiceCreateExcelUtil.createHeader(headerNames, sheet);
            createBody(details, sheet);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            return bos.toByteArray();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error while creating excel file", e);
        }
    }

    public static void createBody(List<OneTimeOrder> details, XSSFSheet sheet) {
        int rowCount = 1;
        for (OneTimeOrder order : details) {
            Customer customer = order.getCustomer();
            CustomerBank customerBank = customer.getCustomerBank();
            BaseAddress baseAddress = customer.getAddress();

            Row row = sheet.createRow(rowCount++);
            row.createCell(0).setCellValue(customer.getTin());
            row.createCell(1).setCellValue(customer.getCompanyName());
            row.createCell(2).setCellValue(baseAddress.getRegion().getNameUzLatin());
            row.createCell(3).setCellValue(baseAddress.getFullAddressAddress());
            row.createCell(4).setCellValue(customer.getPhone());
            row.createCell(5).setCellValue(customerBank.getBank().getCode());
            row.createCell(6).setCellValue(customerBank.getAccountNumber());
            row.createCell(7).setCellValue(order.getNumber());
            row.createCell(8).setCellValue(order.getCreatedDate().toString().substring(0, 10));
            row.createCell(9).setCellValue(order.getTotalSellPrice().doubleValue());
            row.createCell(10).setCellValue(order.getStatus().getName());
        }
    }

    @Override
    public OneTimeOrderResponseDto create(OneTimeRequestCreatDto dto) {
        OneTimeRequestDto oneTimeRequestDto = new OneTimeRequestDto();
        //Set true to calculate NDS
        oneTimeRequestDto.setCalculateWithVat(true);
        oneTimeRequestDto.setContractId(dto.getContractId());
        oneTimeRequestDto.setCustomerId(dto.getCustomerId());
        oneTimeRequestDto.setServiceId(dto.getServiceId());
        oneTimeRequestDto.setQuantity(dto.getQuantity());
        if (dto.getCustomerBranchId() != null) {
            Fiscalization fiscalization = fiscalizationRepository.findFirstByCustomerIdAndCustomerBranchIdAndStatus(dto.getCustomerId(), dto.getCustomerBranchId(), DeviceStatus.FISCAL_ACTIVE);
            if (fiscalization != null) {
                oneTimeRequestDto.setSerialNumberKKM(fiscalization.getDevice().getSerialNumber());
                oneTimeRequestDto.setSerialNumberFM(fiscalization.getFiscalModule().getSerialNumber());
            }
        }
        return getOneTimeOrderResponseDto(oneTimeRequestDto);
    }

    @Override
    public OneTimeOrderResponseDto update(OneTimeRequestDto dto) {
        return getOneTimeOrderResponseDto(dto);
    }

    private OneTimeOrderResponseDto getOneTimeOrderResponseDto(OneTimeRequestDto dto) {
        OneTimeOrder order;
        if (dto.getId() != null) {
            order = findById(dto.getId());
            if (order.getStatus().equals(ContractStatus.ACCEPTED) ||
                    order.getStatus().equals(ContractStatus.CLOSED) ||
                    order.getStatus().equals(ContractStatus.REJECTED))
                throw validationService.statusChangeExceptionThrow(order.getStatus().getName()).get();
        } else {
            order = new OneTimeOrder();
        }

        Customer customer = customerOldService.findById(dto.getCustomerId());
        Contract contract = contractService.findById(dto.getContractId());
        CtsService ctsService = ctsServiceService.findById(dto.getServiceId());
        if (ctsService.getWithQuantity() != null && ctsService.getWithQuantity()) {
            if (dto.getQuantity() == null || dto.getQuantity() <= 0)
                throw validationService.requiredExceptionThrow(QTY).get();
        }

        order.setCustomerId(customer.getId());
        order.setServiceId(ctsService.getId());
        if (order.getId() == null || order.getBonus() == null) {
            order.setBonus(ctsService.getBonus());
        }
        order.setContractId(contract.getId());


        int deviceCount = 1;
        Double price = ctsService.getPrice();
        if (ctsService.getWithQuantity() != null && ctsService.getWithQuantity())
            deviceCount = deviceCount * dto.getQuantity();
        BigDecimal vatPriceFor1 = BigDecimalUtil.calculateVatPriceFor1(BigDecimal.valueOf(price), vatService.getActiveVat().getPercent());
        BigDecimal productPriceFor1 = BigDecimalUtil.calculateProductPriceFor1SubtractVatPrice(BigDecimal.valueOf(price), vatPriceFor1);

        BigDecimal overallPrice = productPriceFor1.multiply(BigDecimal.valueOf(deviceCount));
        BigDecimal overallSellPrice = BigDecimal.valueOf(price).multiply(BigDecimal.valueOf(deviceCount));
        BigDecimal overallVatPrice = vatPriceFor1.multiply(BigDecimal.valueOf(deviceCount));

        order.setTotalPrice(BigDecimalUtil.setScaleByTwo(overallPrice));
        order.setTotalPriceInWord(NumberToWordUtil.digits2Text(BigDecimalUtil.setScaleByTwo(overallPrice)));
        order.setTotalSellPrice(BigDecimalUtil.setScaleByTwo(overallSellPrice));
        order.setTotalSellPriceInWord(NumberToWordUtil.digits2Text(BigDecimalUtil.setScaleByTwo(overallSellPrice)));
        order.setTotalVatPrice(BigDecimalUtil.setScaleByTwo(overallVatPrice));
        order.setTotalVatPriceInWord(NumberToWordUtil.digits2Text(BigDecimalUtil.setScaleByTwo(overallVatPrice)));
        order.setTotalProductCount(deviceCount);
        order.setSerialNumberKKM(dto.getSerialNumberKKM());
        order.setSerialNumberFM(dto.getSerialNumberFM());
        if (order.getId() == null)
            order.setStatus(ContractStatus.WAITING_FOR_DOCUMENT_ATTACHMENT);

        order = save(order);
        log.info("order saved");

        return oneTimeOrderMapper.toDto(order);
    }

    @Override
    public OneTimeOrderResponseDto getById(Long id) {
        return oneTimeOrderMapper.toDto(findById(id));
    }

    @Override
    public OneTimeOrderResponseDto uploadFile(Long id, MultipartFile file) {
        OneTimeOrder order = findById(id);
        UploadFileResponse response = fileUploadService.upload(file, id, EntityTypeEnum.ONE_TIME_ORDER.name(), DocumentType.ONE_TIME_ORDER.name());

        order.setFileName(response.getFileName());
        order.setContentType(file.getContentType());
        order.setFileUri(response.getFileDownloadUri());
        order.setStatus(ContractStatus.DOCUMENT_ATTACHED);
        // update invoice with file
        order = save(order);

        return oneTimeOrderMapper.toDto(order);
    }

    @Override
    public String getSpecificationHtmlTemplate(Long id) {
        return thymeleafService.generateOneTimeSpecHTML(findById(id));
    }

    @Override
    public OneTimeOrderResponseDto changeStatus(OneTimeOrderChangeStatusRequestDto dto) {
        OneTimeOrder order = findById(dto.getId());
        if (order.getStatus().equals(ContractStatus.ACCEPTED) ||
                order.getStatus().equals(ContractStatus.CLOSED) ||
                order.getStatus().equals(ContractStatus.REJECTED))
            throw validationService.statusChangeExceptionThrow(order.getStatus().getName()).get();

        order.setStatus(dto.getStatus());
        order = save(order);
        log.info("order status changed");

        return oneTimeOrderMapper.toDto(order);
    }

    @Override
    public Page<OneTimeOrderResponseDto> getByContractId(Long contractId, BaseFilter filter) {
        return repository.findByContractIdOrderByIdDesc(contractId, filter.getOrderedPageable())
                .map(oneTimeOrderMapper::toDto);
    }
}
