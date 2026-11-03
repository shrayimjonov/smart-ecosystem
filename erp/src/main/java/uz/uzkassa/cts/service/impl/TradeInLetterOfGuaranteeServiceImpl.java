package uz.uzkassa.cts.service.impl;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.dto.contract.request.ContractChangeStatusDto;
import uz.uzkassa.cts.dto.tradeinletterofguarantee.request.LetterOfGuaranteeRequestDto;
import uz.uzkassa.cts.dto.tradeinletterofguarantee.request.LetterOfGuaranteeUpdateRequestDto;
import uz.uzkassa.cts.dto.tradeinletterofguarantee.response.LetterOfGuaranteeResponseDto;
import uz.uzkassa.cts.dto.uploadfile.response.UploadFileResponse;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.DocumentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.TradeInInvoiceFilter;
import uz.uzkassa.cts.mappings.TradeInLetterOfGuaranteeMapper;
import uz.uzkassa.cts.repository.TradeInLetterOfGuaranteeRepository;
import uz.uzkassa.cts.service.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TradeInLetterOfGuaranteeServiceImpl extends BaseServiceImpl<TradeInLetterOfGuarantee, Long> implements TradeInLetterOfGuaranteeService {

    private final TradeInLetterOfGuaranteeRepository repository;
    private final CustomerOldService customerOldService;
    private final TradeInDeviceService tradeInDeviceService;
    private final FileUploadService fileUploadService;
    private final ThymeleafService thymeleafService;
    private final TradeInLetterOfGuaranteeMapper tradeInLetterOfGuaranteeMapper;

    public TradeInLetterOfGuaranteeServiceImpl(TradeInLetterOfGuaranteeRepository repository,
                                               CustomerOldService customerOldService,
                                               TradeInDeviceService tradeInDeviceService,
                                               FileUploadService fileUploadService,
                                               ThymeleafService thymeleafService,
                                               TradeInLetterOfGuaranteeMapper tradeInLetterOfGuaranteeMapper) {
        super(repository);
        this.repository = repository;
        this.customerOldService = customerOldService;
        this.tradeInDeviceService = tradeInDeviceService;
        this.fileUploadService = fileUploadService;
        this.thymeleafService = thymeleafService;
        this.tradeInLetterOfGuaranteeMapper = tradeInLetterOfGuaranteeMapper;
    }

    @Override
    public Page<LetterOfGuaranteeResponseDto> list(TradeInInvoiceFilter filter) {
        Specification<TradeInLetterOfGuarantee> specification = (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);

            ArrayList<Predicate> list = new ArrayList<>();
            if (StringUtils.isNotEmpty(filter.getSearch())) {
                Path<Customer> customer = root.join(TradeInLetterOfGuarantee_.CUSTOMER, JoinType.INNER);
                Path<String> customerName = customer.get(Customer_.NAME);
                Path<String> customerCompanyName = customer.get(Customer_.COMPANY_NAME);
                Path<String> tin = customer.get(Customer_.TIN);

                Predicate customerNamePred = criteriaBuilder.like(criteriaBuilder.lower(customerName), filter.getSearchForQuery());
                Predicate customerCompanyNamePred = criteriaBuilder.like(criteriaBuilder.lower(customerCompanyName), filter.getSearchForQuery());
                Predicate tinPred = criteriaBuilder.like(criteriaBuilder.lower(tin), filter.getSearchForQuery());

                list.add(criteriaBuilder.or(customerNamePred, tinPred, customerCompanyNamePred));
            }
            if (filter.getFrom() != null && filter.getTo() != null) {
                Path<LocalDateTime> createDate = root.get(TradeInLetterOfGuarantee_.CREATED_DATE);
                list.add(criteriaBuilder.between(createDate, filter.getFrom().atStartOfDay(), filter.getTo().atTime(LocalTime.MAX)));
            }
            if (filter.getStatus() != null) {
                list.add(criteriaBuilder.equal(root.get(TradeInLetterOfGuarantee_.STATUS), ContractStatus.valueOf(filter.getStatus())));
            }
            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
        return repository
            .findAll(specification, filter.getOrderedPageable())
            .map(tradeInLetterOfGuaranteeMapper::toDto);
    }

    @Override
    public LetterOfGuaranteeResponseDto getById(Long id) {
        return tradeInLetterOfGuaranteeMapper.toDto(findById(id));
    }

    @Override
    public LetterOfGuaranteeResponseDto create(LetterOfGuaranteeRequestDto dto) {
        Customer customer = customerOldService.findById(dto.getCustomerId());
        TradeInDevice device = tradeInDeviceService.findById(dto.getTradeInDeviceId());

        TradeInLetterOfGuarantee guarantee = new TradeInLetterOfGuarantee();
        guarantee.setDeviceSerialNumber(dto.getDeviceSerialNumber());
        guarantee.setStatus(ContractStatus.CREATED);
        guarantee.setCustomerId(customer.getId());
        guarantee.setTradeInDeviceId(device.getId());

        save(guarantee);

        return tradeInLetterOfGuaranteeMapper.toDto(guarantee);
    }

    @Override
    public String getHtmlTemplate(Long id) {
        TradeInLetterOfGuarantee guarantee = findById(id);
        return thymeleafService.generateTradeLetterOfGuaranteeHTML(guarantee);
    }

    @Override
    public LetterOfGuaranteeResponseDto update(LetterOfGuaranteeUpdateRequestDto dto) {
        TradeInLetterOfGuarantee guarantee = findById(dto.getId());
        if (guarantee.getStatus() == ContractStatus.ACCEPTED ||
            guarantee.getStatus() == ContractStatus.CLOSED) {
            throw validationService.badRequestExceptionThrow(UPLOAD_NOT_ALLOWED_FOR_ACCEPTED_STATUS).get();
        }
        Customer customer = customerOldService.findById(dto.getCustomerId());
        TradeInDevice device = tradeInDeviceService.findById(dto.getTradeInDeviceId());

        guarantee.setCustomerId(customer.getId());
        guarantee.setDeviceSerialNumber(dto.getDeviceSerialNumber());
        guarantee.setTradeInDeviceId(device.getId());

        save(guarantee);

        return tradeInLetterOfGuaranteeMapper.toDto(guarantee);
    }

    @Override
    public LetterOfGuaranteeResponseDto changeStatus(Long id, ContractChangeStatusDto dto) {
        TradeInLetterOfGuarantee guarantee = findById(id);
        if (guarantee.getStatus() == ContractStatus.CLOSED) {
            throw validationService.badRequestExceptionThrow(UPLOAD_NOT_ALLOWED_FOR_ACCEPTED_STATUS).get();
        }
        guarantee.setStatus(dto.getContractStatus());
        save(guarantee);

        return tradeInLetterOfGuaranteeMapper.toDto(guarantee);

    }

    @Override
    public LetterOfGuaranteeResponseDto uploadFile(Long id, MultipartFile file) {
        TradeInLetterOfGuarantee guarantee = findById(id);
        if (guarantee.getStatus() == ContractStatus.ACCEPTED ||
            guarantee.getStatus() == ContractStatus.CLOSED) {
            throw validationService.badRequestExceptionThrow(UPLOAD_NOT_ALLOWED_FOR_ACCEPTED_STATUS).get();
        }

        UploadFileResponse response = fileUploadService.upload(file, id, EntityTypeEnum.TRADE_IN_LETTER_OF_GUARANTEE.name(), DocumentType.TRADE_IN_LETTER_OF_GUARANTEE.name());
        guarantee.setFileName(response.getFileName());
        guarantee.setContentType(file.getContentType());
        guarantee.setFileUri(response.getFileDownloadUri());
        guarantee.setStatus(ContractStatus.DOCUMENT_ATTACHED);
        save(guarantee);

        return tradeInLetterOfGuaranteeMapper.toDto(guarantee);
    }

    @Override
    public List<LetterOfGuaranteeResponseDto> getByCustomerIdForAttach(Long customerId) {
        return repository.findAllTradeInLetterOfGuaranteeByCustomerIdAndStatus(customerId, ContractStatus.ACCEPTED)
            .stream().map(tradeInLetterOfGuaranteeMapper::toDto)
            .collect(Collectors.toList());
    }
}
