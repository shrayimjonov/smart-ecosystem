package uz.uzkassa.cts.service.impl;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.dto.contract.request.ContractChangeStatusDto;
import uz.uzkassa.cts.dto.contract.request.ContractDto;
import uz.uzkassa.cts.dto.contract.request.ContractFilterRequestDto;
import uz.uzkassa.cts.dto.contract.response.ContactResponseDto;
import uz.uzkassa.cts.dto.uploadfile.response.UploadFileResponse;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.mappings.ContractMapper;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static uz.uzkassa.cts.enums.ThymeleafTemplateType.*;

@Service
@Transactional
@Slf4j
public class ContractServiceImpl extends BaseServiceImpl<Contract, Long> implements ContractService {

    private final ContractRepository repository;
    private final CompanyRepository companyRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final FileStorageService fileStorageService;
    private final ThymeleafService thymeleafService;
    private final FileUploadService fileUploadService;
    private final ContractMapper contractMapper;
    private final CustomerEmployeeRepository customerEmployeeRepository;
    private final AddressService addressService;

    public ContractServiceImpl(ContractRepository repository,
                               CompanyRepository companyRepository,
                               CustomerRepository customerRepository,
                               UserRepository userRepository, AuthorityRepository authorityRepository,
                               FileStorageService fileStorageService,
                               ThymeleafService thymeleafService,
                               FileUploadService fileUploadService,
                               ContractMapper contractMapper, CustomerEmployeeRepository customerEmployeeRepository, AddressService addressService) {
        super(repository);
        this.repository = repository;
        this.companyRepository = companyRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.fileStorageService = fileStorageService;
        this.thymeleafService = thymeleafService;
        this.fileUploadService = fileUploadService;
        this.contractMapper = contractMapper;
        this.customerEmployeeRepository = customerEmployeeRepository;
        this.addressService = addressService;
    }


    private ContactResponseDto create(ContractDto contractDto, ThymeleafTemplateType templateTypeEnum) {
        Long companyId = getCompanyId();
        Company company = companyRepository.findById(companyId).orElseThrow(validationService.entityNotFoundExceptionThrow(COMPANY));
        Customer customer = customerRepository.findById(contractDto.getCustomerId()).orElseThrow(validationService.entityNotFoundExceptionThrow(CUSTOMER));

        long docNumber = 1L;
        Contract contract = repository.findFirstByTemplateTypeEnumAndCustomer_IdOrderByIdDesc(templateTypeEnum, customer.getId()).orElse(null);
        if (contract != null) {
            throw validationService.entityAlreadyExistExceptionThrow(CONTRACT).get();
        } else {
            contract = new Contract();
            contract.setDocNumber(docNumber);
        }

        contract.setBranchId(SecurityUtils.getBranchId());

        assignDocumentData(contract, templateTypeEnum, customer.getId());
        assignCompany(company, contract);
        assignCustomer(customer, contract);

        Contract newContract = save(contract);

        return contractMapper.toDTO(newContract);
    }


    @Override
    public ContactResponseDto createContractForBuy(ContractDto contractDto) {
        return create(contractDto, CONTRACT_FOR_BUY);
    }

    @Override
    public ContactResponseDto createContractForLeasing(ContractDto contractDto) {
        return create(contractDto, CONTRACT_FOR_LEASING);
    }

    @Override
    public ContactResponseDto createContractForRent(ContractDto contractDto) {
        return create(contractDto, CONTRACT_FOR_RENT);
    }

    @Override
    public ContactResponseDto createContractForService(ContractDto contractDto) {
        return create(contractDto, CONTRACT_FOR_SERVICE);
    }

    @Override
    public ContactResponseDto update(ContractDto contractDto) {
        Contract contract = repository.findById(contractDto.getId())
                .orElseThrow(validationService.entityNotFoundExceptionThrow(CONTRACT));

        if (contract.getStatus() != ContractStatus.DOCUMENT_ATTACHED && contract.getStatus() != ContractStatus.CREATED)
            throw validationService.entityAlreadyExistExceptionThrow(CONTRACT).get();

        Company company = companyRepository.findById(getCompanyId())
                .orElseThrow(validationService.entityNotFoundExceptionThrow(COMPANY));
        Customer customer = customerRepository.findById(contractDto.getCustomerId())
                .orElseThrow(validationService.entityNotFoundExceptionThrow(CUSTOMER));

        contract.setBranchId(SecurityUtils.getBranchId());
        contract.setCompanyId(getCompanyId());

        assignDocumentData(contract, contract.getTemplateTypeEnum(), customer.getId());
        assignCompany(company, contract);
        assignCustomer(customer, contract);

        Contract newContract = save(contract);
        return contractMapper.toDTO(newContract);
    }

    @Override
    public ContactResponseDto get(Long id) {
        Contract contract = findById(id);
        return contractMapper.toDTO(contract);
    }

    @Override
    public Page<ContactResponseDto> listFrCustomer(ContractFilterRequestDto dto, BaseFilter filter) {
        /*Long id = SecurityUtils.getCurrentUserId();

        User user = userRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(USER));
        dto.setCustomerId(user.getCustomerId());*/
        return getFilteredList(dto, filter);
    }

    @Override
    public Page<ContactResponseDto> listForCto(ContractFilterRequestDto dto, BaseFilter filter) {
        return getFilteredList(dto, filter);
    }

    @Override
    public UploadFileResponse uploadFile(MultipartFile file, Long id) {
        Contract contract = findById(id);
        UploadFileResponse response = fileUploadService.upload(file, id, EntityTypeEnum.CONTRACT.name(), DocumentType.CONTRACT.name());

        contract.setFileName(response.getFileName());
        contract.setContentType(file.getContentType());
        contract.setFileUri(response.getFileDownloadUri());
        contract.setStatus(ContractStatus.DOCUMENT_ATTACHED);
        // update contract with file
        save(contract);

        return response;
    }

    @Override
    public Resource loadFileAsResource(String tin, String fileName) {
        return fileStorageService.loadFileAsResource(tin, fileName);
    }

    @Override
    public List<ContactResponseDto> getByCustomerId(Long id) {
        List<Contract> contractList = repository.findByCustomerIdAndTemplateTypeEnum(id, CONTRACT_FOR_SERVICE);

        return contractList.stream().map(contractMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public ContactResponseDto changeContractStatus(Long id, ContractChangeStatusDto dto) {

        Contract contract = findById(id);

        if (!contract.getCustomer().getId().equals(dto.getCustomerId()))
            throw validationService.badRequestExceptionThrow(CUSTOMER_CONTRACT_UPDATE_DENIED).get();

        contract.setStatus(dto.getContractStatus());

        Contract updContract = save(contract);

        return contractMapper.toDTO(updContract);
    }

    @Override
    public String getContractForBuyHtmlTemplate(Long id) {
        Contract contract = findById(id);
        return thymeleafService.generateContractForBuyHTML(contract);
    }

    @Override
    public ContactResponseDto getCustomerActiveContractForBuy(Long id) {
        return contractMapper.toDTO(getContactByCustomerAndTemplateType(CONTRACT_FOR_BUY, id));
    }

    @Override
    public ContactResponseDto getCustomerActiveContractForService(Long id) {
        return contractMapper.toDTO(getContactByCustomerAndTemplateType(CONTRACT_FOR_SERVICE, id));
    }

    @Override
    public ContactResponseDto getCustomerActiveContractForLeasing(Long id) {
        return contractMapper.toDTO(getContactByCustomerAndTemplateType(CONTRACT_FOR_LEASING, id));
    }

    @Override
    public ContactResponseDto getCustomerActiveContractForRent(Long id) {
        return contractMapper.toDTO(getContactByCustomerAndTemplateType(CONTRACT_FOR_RENT, id));
    }

    @Override
    public Contract findByCustomerId(Long customerId) {
        return repository.findByCustomerId(customerId);
    }

    private Contract getContactByCustomerAndTemplateType(ThymeleafTemplateType templateTypeEnum, Long customerId) {
        Contract contract = repository.findFirstByTemplateTypeEnumAndCustomer_IdOrderByIdDesc(templateTypeEnum, customerId)
                .orElseThrow(validationService.entityNotFoundExceptionThrow(CONTRACT));

        if (contract.getStatus() == ContractStatus.CLOSED || contract.getStatus() == ContractStatus.REJECTED)
            throw validationService.entityNotFoundExceptionThrow(CONTRACT).get();
        return contract;
    }

    private Page<ContactResponseDto> getFilteredList(ContractFilterRequestDto dto, BaseFilter filter) {
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(),
                Sort.by(SortType.asc.equals(filter.getSortOrder()) ? Sort.Order.asc(filter.getOrderBy()) : Sort.Order.desc(filter.getOrderBy())));

        Specification<Contract> specification = (root, criteriaQuery, criteriaBuilder) -> {

            Path<ContractStatus> contractStatus = root.get("status");
            Path<String> name = root.get("name");
            Path<Long> docNumber = root.get("docNumber");
            Path<LocalDate> docDate = root.get("docDate");
            Path<LocalDate> docEndDate = root.get("docEndDate");
            Path<ThymeleafTemplateType> templateTypeEnum = root.get("templateTypeEnum");
            Path<String> customerName = root.get("customerName");
            Path<String> customerAddress = root.get("customerAddress");
            Path<String> customerAccountNumber = root.get("customerAccountNumber");
            Path<String> customerTin = root.get("customerTin");
            Path<String> customerPhoneNumber = root.get("customerPhoneNumber");
            Path<String> customerDirectorFullName = root.get("customerDirectorFullName");
            Path<Long> customerId = root.get("customerId");

            ArrayList<Predicate> list = new ArrayList<>();

            if (dto.getCustomerId() != null)
                list.add(criteriaBuilder.equal(customerId, dto.getCustomerId()));
            if (dto.getStatus() != null)
                list.add(criteriaBuilder.equal(contractStatus, dto.getStatus()));
            if (dto.getDocNumber() != null)
                list.add(criteriaBuilder.equal(docNumber, dto.getDocNumber()));
            if (dto.getDocDate() != null)
                list.add(criteriaBuilder.equal(docDate, dto.getDocDate()));
            if (dto.getDocEndDate() != null)
                list.add(criteriaBuilder.equal(docEndDate, dto.getDocEndDate()));
            if (dto.getTemplateTypeEnum() != null)
                list.add(criteriaBuilder.equal(templateTypeEnum, dto.getTemplateTypeEnum()));
            if (StringUtils.isNotEmpty(filter.getSearch())) {
                list.add(criteriaBuilder.like(name, "%" + filter.getSearch() + "%"));
                list.add(criteriaBuilder.like(customerAccountNumber, "%" + filter.getSearch() + "%"));
                list.add(criteriaBuilder.like(customerTin, "%" + filter.getSearch() + "%"));
                list.add(criteriaBuilder.like(customerName, "%" + filter.getSearch() + "%"));
                list.add(criteriaBuilder.like(customerAddress, "%" + filter.getSearch() + "%"));
                list.add(criteriaBuilder.like(customerPhoneNumber, "%" + filter.getSearch() + "%"));
                list.add(criteriaBuilder.like(customerDirectorFullName, "%" + filter.getSearch() + "%"));
            }

            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };

        Page<Contract> contracts = repository.findAll(specification, pageable);

        return contracts.map(contractMapper::toDTO);
    }

    private void assignDocumentData(Contract contract, ThymeleafTemplateType templateTypeEnum, Long customerId) {
        LocalDate contractDate = LocalDate.now();
        contract.setDocDate(contractDate);
        contract.setDocEndDate(contractDate.plusYears(1));
        contract.setTemplateTypeEnum(templateTypeEnum);
        contract.setStatus(ContractStatus.CREATED);

        Long docNumber = contract.getDocNumber();
        contract.setDocNumber(docNumber);
        contract.setName(generateName(docNumber, templateTypeEnum, contract.getDocDate(), customerId));
    }

    private void assignCustomer(Customer customer, Contract contract) {
        //Bank customerBank = customer.getBank();
        CustomerBank customerBank = customer.getCustomerBank();
        if (customerBank == null)
            throw validationService.entityNotFoundExceptionThrow(BANK).get();

        contract.setCustomerId(customer.getId());
        contract.setCustomerAccountNumber(customerBank.getAccountNumber());
        contract.setCustomerBankMFO(customerBank.getBank().getCode());
        contract.setCustomerAddress(customer.getAddress().getFullAddressAddress(AddressSeparatorType.DOT));
        contract.setCustomerBankName(customerBank.getBank().getNameNotNullable());
        contract.setCustomerName(customer.getName());
        contract.setCustomerTin(customer.getTin());
        contract.setCustomerPhoneNumber(customer.getPhone());
        customerEmployeeRepository.findFirstByCustomerIdAndRole(customer.getId(), Role.DIRECTOR).ifPresent(customerEmployee -> contract.setCustomerDirectorFullName(customerEmployee.getName()));
        contract.setCustomerOKED(customer.getOked());
    }

    private void assignCompany(Company company, Contract contract) {
        CompanyBank companyBank = company.getCompanyBank();
        if (companyBank == null)
            throw validationService.entityNotFoundExceptionThrow(BANK).get();

        contract.setCompanyId(company.getId());
        contract.setCompanyAccountNumber(companyBank.getAccountNumber());
        contract.setCompanyName(company.getName());
        Optional.ofNullable(addressService.getLegalAddress(company.getEntityId(), company.getEntityType()))
                .ifPresent(address -> contract.setCompanyAddress(address.getFullAddressAddress(AddressSeparatorType.DOT)));
        contract.setCompanyBankMFO(companyBank.getBank().getCode());
        contract.setCompanyBankName(companyBank.getBank().getNameNotNullable());
        contract.setCompanyTin(company.getTin());
        contract.setCompanyPhoneNumber(company.getPhone());
        contract.setCompanyOKED(companyBank.getOked());
        userRepository.findFirstByCompanyIdAndStatusAndAuthoritiesInAndDeletedIsFalse(company.getId(), UserStatus.ACTIVE, Set.of(authorityRepository.findById(Role.DIRECTOR.getRoleCode()).get())).ifPresent(user -> contract.setCompanyDirectorFullName(user.getFullName()));
    }

    private String generateName(Long docNumber, ThymeleafTemplateType templateTypeEnum, LocalDate date, Long customerId) {

        StringBuilder builder = new StringBuilder();
        switch (templateTypeEnum) {
            case CONTRACT_FOR_BUY:
                builder.append(PrefixEnum.CB);
                break;
            case CONTRACT_FOR_SERVICE:
                builder.append(PrefixEnum.CS);
                break;
            case CONTRACT_FOR_LEASING:
                builder.append(PrefixEnum.CL);
                break;
            case CONTRACT_FOR_RENT:
                builder.append(PrefixEnum.CR);
                break;
        }
        builder.append("/");
        builder.append(customerId.toString());
        builder.append("/");
        builder.append(getTwoDigitYearFormat(date));
        builder.append("/");
        builder.append(StringUtils.leftPad(docNumber.toString(), 7, '0'));

        return builder.toString();
    }

    private String getTwoDigitYearFormat(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy");
        return date.format(formatter);
    }
}
