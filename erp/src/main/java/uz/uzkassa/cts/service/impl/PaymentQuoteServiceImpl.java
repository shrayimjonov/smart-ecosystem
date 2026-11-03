package uz.uzkassa.cts.service.impl;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.cts.domain.PaymentQuote_;
import uz.uzkassa.cts.domain.TradeInPriceHistory;
import uz.uzkassa.cts.domain.address.BaseAddress;
import uz.uzkassa.cts.dto.paymentQuote.PaymentQuoteDTO;
import uz.uzkassa.cts.dto.paymentQuote.PaymentQuoteDetailDTO;
import uz.uzkassa.cts.dto.paymentQuote.PaymentQuoteTemplateDTO;
import uz.uzkassa.cts.enums.ContractType;
import uz.uzkassa.cts.enums.PaymentQuote;
import uz.uzkassa.cts.enums.PaymentQuoteStatus;
import uz.uzkassa.cts.enums.PaymentQuoteType;
import uz.uzkassa.cts.filter.PaymentQuoteFilter;
import uz.uzkassa.cts.mappings.AddressMapper;
import uz.uzkassa.cts.mappings.PaymentQuoteMapper;
import uz.uzkassa.cts.repository.BaseRepository;
import uz.uzkassa.cts.repository.PaymentQuoteRepository;
import uz.uzkassa.cts.repository.TradeInPriceHistoryRepository;
import uz.uzkassa.cts.service.PaymentQuoteService;
import uz.uzkassa.cts.service.ThymeleafService;
import uz.uzkassa.cts.utils.DateUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
@Slf4j
public class PaymentQuoteServiceImpl extends BaseServiceImpl<uz.uzkassa.cts.domain.PaymentQuote, Long> implements PaymentQuoteService {

    private final PaymentQuoteRepository paymentQuoteRepository;
    private final ThymeleafService thymeleafService;
    private final TradeInPriceHistoryRepository tradeInPriceHistoryRepository;
    private final AddressMapper addressMapper;
    private final PaymentQuoteMapper paymentQuoteMapper;

    public PaymentQuoteServiceImpl(BaseRepository<uz.uzkassa.cts.domain.PaymentQuote, Long> repository,
                                   PaymentQuoteRepository paymentQuoteRepository,
                                   ThymeleafService thymeleafService,
                                   TradeInPriceHistoryRepository tradeInPriceHistoryRepository,
                                   AddressMapper addressMapper,
                                   PaymentQuoteMapper paymentQuoteMapper) {
        super(repository);
        this.paymentQuoteRepository = paymentQuoteRepository;
        this.thymeleafService = thymeleafService;
        this.tradeInPriceHistoryRepository = tradeInPriceHistoryRepository;
        this.addressMapper = addressMapper;
        this.paymentQuoteMapper = paymentQuoteMapper;
    }

    @Override
    @Transactional
    public Long create(PaymentQuoteDTO paymentQuoteDTO) {
        if (!CollectionUtils.isEmpty(paymentQuoteRepository.findAllByTinAndStatusAndPaymentContractsIsNull(paymentQuoteDTO.getTin(), PaymentQuoteStatus.PENDING))) {
            throw validationService.badRequestExceptionThrow(PAYMENT_QUOTE_ERROR).get();
        }
        uz.uzkassa.cts.domain.PaymentQuote paymentQuote = new uz.uzkassa.cts.domain.PaymentQuote();
        paymentQuote.setAddress(addressMapper.toEntity(paymentQuoteDTO.getAddress()));
        paymentQuote.setQty(paymentQuoteDTO.getQty());
        paymentQuote.setTin(paymentQuoteDTO.getTin());
        paymentQuote.setCustomerName(paymentQuoteDTO.getCustomerName());
        paymentQuote.setModelId(paymentQuoteDTO.getModelId());
        paymentQuote.setDirector(paymentQuoteDTO.getDirector());
        paymentQuote.setPhone(paymentQuoteDTO.getPhone());
        paymentQuote.setDescription(paymentQuoteDTO.getDescription());
        paymentQuote.setPaymentType(paymentQuoteDTO.getPaymentType());

        paymentQuote = paymentQuoteRepository.save(paymentQuote);
        return paymentQuote.getId();
    }

    @Override
    public Long update(Long id, Long modelId) {
        uz.uzkassa.cts.domain.PaymentQuote paymentQuote = findById(id);
        if (paymentQuote.getStatus() == PaymentQuoteStatus.PENDING) {
            paymentQuote.setModelId(modelId);
            save(paymentQuote);
            return paymentQuote.getId();
        } else {
            throw validationService.badRequestExceptionThrow(PAYMENT_QUOTE_MODEL).get();
        }
    }

    @Override
    public PaymentQuoteDetailDTO get(Long id) {
        return paymentQuoteMapper.toDto(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentQuoteDetailDTO> list(PaymentQuoteFilter filter) {
        Specification<uz.uzkassa.cts.domain.PaymentQuote> specification = (root, criteriaQuery, builder) -> {
            criteriaQuery.distinct(true);

            ArrayList<Predicate> list = new ArrayList<>();

            if (StringUtils.isNotEmpty(filter.getSearch())) {
                Predicate tin = builder.like(builder.lower(root.get(PaymentQuote_.TIN)), filter.getSearchForQuery());
                Predicate name = builder.like(builder.lower(root.get(PaymentQuote_.CUSTOMER_NAME)), filter.getSearchForQuery());
                Predicate director = builder.like(builder.lower(root.get(PaymentQuote_.DIRECTOR)), filter.getSearchForQuery());
                Predicate phone = builder.like(builder.lower(root.get(PaymentQuote_.PHONE)), filter.getSearchForQuery());
                list.add(builder.or(tin, name, director, phone));
            }
            if (filter.getStatus() != null) {
                Path<PaymentQuoteStatus> status = root.get(PaymentQuote_.STATUS);
                list.add(builder.equal(status, PaymentQuoteStatus.valueOf(filter.getStatus())));
            }
            if (filter.getPaymentStatus() != null) {
                Path<PaymentQuote> paymentStatus = root.get(PaymentQuote_.PAYMENT_STATUS);
                list.add(builder.equal(paymentStatus, PaymentQuote.valueOf(filter.getPaymentStatus())));
            }
            if (filter.getPaymentType() != null) {
                Path<PaymentQuoteType> paymentType = root.get(PaymentQuote_.PAYMENT_TYPE);
                list.add(builder.equal(paymentType, PaymentQuoteType.valueOf(filter.getPaymentType())));
            }
            if (filter.getFrom() != null && filter.getTo() != null) {
                Path<LocalDateTime> createDate = root.get(PaymentQuote_.CREATED_DATE);
                list.add(builder.between(createDate, filter.getFrom().atStartOfDay(), filter.getTo().atTime(LocalTime.MAX)));
            }
            if (filter.getRegionId() != null) {
                Path<BaseAddress> addressPath = root.get("address");
                Path<Long> regionId = addressPath.get("regionId");
                list.add(builder.equal(regionId, filter.getRegionId()));
            }
            if (filter.getModelId() != null) {
                Path<Long> longPath = root.get(PaymentQuote_.MODEL_ID);
                list.add(builder.equal(longPath, filter.getModelId()));
            }
            if (!CollectionUtils.isEmpty(filter.getCreatorIds())) {
                list.add(builder.in(root.get(PaymentQuote_.CREATOR_ID)).value(filter.getCreatorIds()));
            }
            return builder.and(list.toArray(new Predicate[0]));
        };

        return paymentQuoteRepository.findAll(specification, filter.getOrderedPageable())
                .map(paymentQuoteMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentQuoteDetailDTO> paymentQuoteListByTin(String tin) {
        return paymentQuoteRepository.findAllByTinAndStatusAndPaymentContractsIsNull(tin, PaymentQuoteStatus.PENDING).stream().map(paymentQuoteMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public String generateHtmlTemplate(Long id) {
        uz.uzkassa.cts.domain.PaymentQuote paymentQuote = findById(id);
        if (PaymentQuoteType.LEASING_PRIME == paymentQuote.getPaymentType()) {
            return thymeleafService.generatePaymentQuoteLeasingHTML(paymentQuote);
        }

        PaymentQuoteTemplateDTO paymentQuoteTemplateDTO = new PaymentQuoteTemplateDTO();
        paymentQuoteTemplateDTO.setId(paymentQuote.getId());
        paymentQuoteTemplateDTO.setTin(paymentQuote.getTin());
        paymentQuoteTemplateDTO.setCreatedDate(DateUtil.getLocalDateTimeAsStringWithoutSecond(paymentQuote.getCreatedDate()));
        paymentQuoteTemplateDTO.setCustomerName(paymentQuote.getCustomerName());
        paymentQuoteTemplateDTO.setModelName(paymentQuote.getModel().getName());
        paymentQuoteTemplateDTO.setFullAddress(paymentQuote.getAddress().getFullAddressAddress());
        paymentQuoteTemplateDTO.setQty(paymentQuote.getQty());

        TradeInPriceHistory tradeInPriceHistory = tradeInPriceHistoryRepository.findFirstByActiveTrueAndContractTypeAndDeviceModelId(ContractType.PAYMENT_CONTRACT, paymentQuote.getModelId());
        if (tradeInPriceHistory == null) {
            throw validationService.badRequestExceptionThrow(PAYMENT_CONTRACT_DEVICE_MODEL).get();
        }
        paymentQuoteTemplateDTO.setTotalVatPrice(tradeInPriceHistory.getVat1pc().multiply(BigDecimal.valueOf(paymentQuote.getQty())));
        paymentQuoteTemplateDTO.setPriceWithVat(tradeInPriceHistory.getPriceWithVat());
        paymentQuoteTemplateDTO.setPrice(tradeInPriceHistory.getPrice1pc());
        paymentQuoteTemplateDTO.setTotalPrice(tradeInPriceHistory.getPrice1pc().multiply(BigDecimal.valueOf(paymentQuote.getQty())));
        paymentQuoteTemplateDTO.setTotalPriceWithVat(tradeInPriceHistory.getPrice1pcWithVat().multiply(BigDecimal.valueOf(paymentQuote.getQty())));

        return thymeleafService.generatePaymentQuoteHTML(paymentQuoteTemplateDTO);
    }

    @Override
    public void updateStatus(Long id, PaymentQuoteStatus status) {
        paymentQuoteRepository.findById(id).ifPresentOrElse(paymentQuote -> {
            paymentQuote.setStatus(status);
            save(paymentQuote);
        }, () -> validationService.entityNotFoundExceptionThrow(PAYMENT_QUOTE).get());
    }

    @Override
    public void updatePaymentStatus(Long id, PaymentQuote paymentStatus) {
        paymentQuoteRepository.findById(id).ifPresentOrElse(paymentQuote -> {
            paymentQuote.setPaymentStatus(paymentStatus);
            save(paymentQuote);
        }, () -> validationService.entityNotFoundExceptionThrow(PAYMENT_QUOTE).get());
    }

    @Override
    public void updatePaymentType(Long id, PaymentQuoteType paymentType) {
        paymentQuoteRepository.findById(id).ifPresentOrElse(paymentQuote -> {
            if (PaymentQuoteStatus.ACCEPTED == paymentQuote.getStatus() || PaymentQuoteStatus.REJECTED == paymentQuote.getStatus()) {
                throw validationService.badRequestExceptionThrow(CANNOT_CHANGE_TYPE, paymentQuote.getStatus().getName()).get();
            }
            paymentQuote.setPaymentType(paymentType);
            save(paymentQuote);
        }, () -> validationService.entityNotFoundExceptionThrow(PAYMENT_QUOTE).get());
    }
}
