package uz.uzkassa.cts.service.impl;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.dto.paymentCompanyContract.PaymentCompanyContractDTO;
import uz.uzkassa.cts.dto.paymentCompanyContract.PaymentCompanyContractDetailDTO;
import uz.uzkassa.cts.dto.paymentCompanyContract.PaymentCompanyContractTemplateDTO;
import uz.uzkassa.cts.dto.uploadfile.response.UploadFileResponse;
import uz.uzkassa.cts.dto.vat.VatInvoiceCalculationDto;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.mappings.PaymentCompanyContractMapper;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.service.*;
import uz.uzkassa.cts.utils.CalcUtils;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uz.uzkassa.cts.utils.PredicateUtil.with;

@Transactional
@Service
@Slf4j
public class PaymentCompanyContractServiceImpl extends BaseServiceImpl<PaymentCompanyContract, Long> implements PaymentCompanyContractService {

    private final PaymentCompanyContractRepository paymentCompanyContractRepository;
    private final ThymeleafService thymeleafService;
    private final FileUploadService fileUploadService;
    private final ModelPriceHistoryRepository modelPriceHistoryRepository;
    private final VatService vatService;
    private final UserRepository userRepository;
    private final DeviceService deviceService;
    private final CompanyDeviceRepository companyDeviceRepository;
    private final CustomerRepository customerRepository;
    private final PaymentCompanyContractMapper paymentCompanyContractMapper;
    private final CustomerEmployeeRepository customerEmployeeRepository;
    private final AddressService addressService;

    public PaymentCompanyContractServiceImpl(BaseRepository<PaymentCompanyContract, Long> repository,
                                             PaymentCompanyContractRepository paymentCompanyContractRepository,
                                             ThymeleafService thymeleafService,
                                             FileUploadService fileUploadService,
                                             ModelPriceHistoryRepository modelPriceHistoryRepository,
                                             VatService vatService,
                                             UserRepository userRepository,
                                             DeviceService deviceService,
                                             CompanyDeviceRepository companyDeviceRepository,
                                             CustomerRepository customerRepository,
                                             PaymentCompanyContractMapper paymentCompanyContractMapper, CustomerEmployeeRepository customerEmployeeRepository, AddressService addressService) {
        super(repository);
        this.paymentCompanyContractRepository = paymentCompanyContractRepository;
        this.thymeleafService = thymeleafService;
        this.fileUploadService = fileUploadService;
        this.modelPriceHistoryRepository = modelPriceHistoryRepository;
        this.vatService = vatService;
        this.userRepository = userRepository;
        this.deviceService = deviceService;
        this.companyDeviceRepository = companyDeviceRepository;
        this.customerRepository = customerRepository;
        this.paymentCompanyContractMapper = paymentCompanyContractMapper;
        this.customerEmployeeRepository = customerEmployeeRepository;
        this.addressService = addressService;
    }

    @Override
    @Transactional
    public PaymentCompanyContractDetailDTO create(PaymentCompanyContractDTO paymentCompanyContractDTO) {
        Device device = deviceService.findById(paymentCompanyContractDTO.getDeviceId());

        PaymentCompanyContract paymentCompanyContract = new PaymentCompanyContract();
        paymentCompanyContract.setDeviceId(paymentCompanyContract.getDeviceId());
        paymentCompanyContract.setStatus(ContractStatus.WAITING_FOR_DOCUMENT_ATTACHMENT);
        paymentCompanyContract.setPaymentCompanyQuoteId(paymentCompanyContractDTO.getPaymentCompanyQuoteId());
        paymentCompanyContract.setCompanyId(paymentCompanyContractDTO.getCompanyId());
        paymentCompanyContract = save(paymentCompanyContract);

        CompanyDevice companyDevice = new CompanyDevice();
        companyDevice.setCompanyId(paymentCompanyContractDTO.getCompanyId());
        companyDevice.setStatus(DeviceStatus.REGISTERED);
        companyDevice.setDeviceId(device.getId());
        companyDeviceRepository.save(companyDevice);

        return paymentCompanyContractMapper.toDto(paymentCompanyContract);
    }

    @Override
    public PaymentCompanyContractDetailDTO getById(Long id) {
        return paymentCompanyContractMapper.toDto(findById(id));
    }

    @Override
    public List<PaymentCompanyContractDetailDTO> getByCompanyId(Long id) {
        return paymentCompanyContractRepository
                .findByCompanyId(id)
                .stream()
                .map(paymentCompanyContractMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentCompanyContractDetailDTO> getContractsByStatusIsAccepted(Long id) {
        return paymentCompanyContractRepository
                .findByCompanyIdAndStatus(id, ContractStatus.ACCEPTED)
                .stream()
                .map(paymentCompanyContractMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<PaymentCompanyContractDetailDTO> list(BaseFilter filter) {
        Specification<PaymentCompanyContract> specification = (root, criteriaQuery, builder) -> {
            criteriaQuery.distinct(true);

            Predicate and = builder.and(Stream.of(
                    with(filter::getCompanyId, a -> builder.equal(root.get(PaymentCompanyContract_.COMPANY_ID), a))
            ).filter(Objects::nonNull).toArray(Predicate[]::new));

            Path<Company> companyPath = root.join(PaymentCompanyContract_.COMPANY, JoinType.INNER);
            Predicate or = builder.or(Stream.of(
                    with(filter::getSearch, a -> builder.like(builder.lower(companyPath.get(Company_.NAME)), a)),
                    with(filter::getSearch, a -> builder.like(builder.lower(companyPath.get(Company_.TIN)), a))
            ).filter(Objects::nonNull).toArray(Predicate[]::new));
            return builder.or(and, or);
        };

        return paymentCompanyContractRepository.findAll(specification, filter.getOrderedPageable())
                .map(paymentCompanyContractMapper::toDto);
    }

    @Override
    public String generateContractHtml(Long id) {
        PaymentCompanyContract paymentCompanyContract = findById(id);
        Company company = paymentCompanyContract.getCompany();
        CompanyBank bank = paymentCompanyContract.getCompany().getCompanyBank();
        if (bank == null) {
            throw validationService.entityNotFoundExceptionThrow(BANK).get();
        }
        if (bank.getAccountNumber() == null) {
            throw validationService.entityNotFoundExceptionThrow(ACCOUNT_NUMBER).get();
        }

        Customer customer = customerRepository.findByTin(company.getTin());
        if (customer == null) {
            throw validationService.badRequestExceptionThrow(NO_DIRECTOR, company.getName()).get();
        }
        PaymentCompanyContractTemplateDTO contractTemplateDTO = new PaymentCompanyContractTemplateDTO();
        contractTemplateDTO.setId(paymentCompanyContract.getId());
        contractTemplateDTO.setContractNumber(paymentCompanyContract.getNumber());
        contractTemplateDTO.setTin(company.getTin());
        contractTemplateDTO.setCreatedDate(DateUtil.getLocalDateTimeAsStringWithoutSecond(paymentCompanyContract.getCreatedDate()));
        contractTemplateDTO.setCompanyName(company.getName());
        contractTemplateDTO.setPhone(company.getPhone());
        contractTemplateDTO.setOked(bank.getOked());
        contractTemplateDTO.setAccountNumber(bank.getAccountNumber());
        contractTemplateDTO.setBankName(bank.getBank().getNameNotNullable());
        contractTemplateDTO.setModelName(paymentCompanyContract.getDevice().getModel().getName());
        Optional.ofNullable(addressService.getLegalAddress(company.getEntityId(), company.getEntityType()))
                .ifPresent(address -> {
                    contractTemplateDTO.setRegion(address.getRegion().getNameRu());
                    contractTemplateDTO.setDistrict(address.getDistrict().getNameRu());
                    contractTemplateDTO.setStreet(address.getStreet());
                    contractTemplateDTO.setHouse(address.getHouse());
                    contractTemplateDTO.setApartment(address.getApartment());
                });
        contractTemplateDTO.setQty(paymentCompanyContract.getPaymentCompanyQuote().getQty());

        customerEmployeeRepository.findFirstByCustomerIdAndRole(customer.getId(), Role.DIRECTOR).ifPresent(customerEmployee -> contractTemplateDTO.setOwnerName(customerEmployee.getName()));

        ModelPriceHistory modelPriceHistory = modelPriceHistoryRepository.findTopByModelIdOrderByIdDesc(paymentCompanyContract.getDevice().getModelId());
        VatInvoiceCalculationDto calculationVatDto = CalcUtils.calculateInvoice(modelPriceHistory.getPrice(), paymentCompanyContract.getPaymentCompanyQuote().getQty(), vatService.getActiveVat().getPercent());

        contractTemplateDTO.setVatRate(calculationVatDto.getVatRate());
        contractTemplateDTO.setVat(calculationVatDto.getVat());
        contractTemplateDTO.setPrice(calculationVatDto.getPrice());
        contractTemplateDTO.setPriceWithVat(calculationVatDto.getPriceWithVat());
        contractTemplateDTO.setTotalVat(calculationVatDto.getTotalVat());
        contractTemplateDTO.setTotalPrice(calculationVatDto.getTotalPrice());
        contractTemplateDTO.setTotalPriceWithVat(calculationVatDto.getTotalPriceWithVat());

        return thymeleafService.generatePaymentCompanyContractHTML(contractTemplateDTO);
    }

    @Override
    public PaymentCompanyContractDetailDTO uploadContractFile(Long id, MultipartFile file) {
        PaymentCompanyContract PaymentCompanyContract = findById(id);

        UploadFileResponse response = fileUploadService.upload(file, id, EntityTypeEnum.PAYMENT_COMPANY_CONTRACT.name(), DocumentType.PAYMENT_COMPANY_CONTRACT.name());
        PaymentCompanyContract.setFileName(response.getFileName());
        PaymentCompanyContract.setFileUri(response.getFileDownloadUri());
        PaymentCompanyContract.setStatus(ContractStatus.DOCUMENT_ATTACHED);
        save(PaymentCompanyContract);
        return paymentCompanyContractMapper.toDto(PaymentCompanyContract);
    }
}
