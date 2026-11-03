package uz.uzkassa.cts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.dto.paymentCompanyQuote.PaymentCompanyQuoteDTO;
import uz.uzkassa.cts.dto.paymentCompanyQuote.PaymentCompanyQuoteDetailDTO;
import uz.uzkassa.cts.dto.paymentCompanyQuote.PaymentCompanyQuoteTemplateDTO;
import uz.uzkassa.cts.dto.vat.VatInvoiceCalculationDto;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.mappings.PaymentCompanyQuoteMapper;
import uz.uzkassa.cts.repository.BaseRepository;
import uz.uzkassa.cts.repository.ModelPriceHistoryRepository;
import uz.uzkassa.cts.repository.PaymentCompanyQuoteRepository;
import uz.uzkassa.cts.service.AddressService;
import uz.uzkassa.cts.service.PaymentCompanyQuoteService;
import uz.uzkassa.cts.service.ThymeleafService;
import uz.uzkassa.cts.service.VatService;
import uz.uzkassa.cts.utils.CalcUtils;
import uz.uzkassa.cts.utils.DateUtil;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uz.uzkassa.cts.utils.PredicateUtil.with;

@Transactional
@Service
@Slf4j
public class PaymentCompanyQuoteServiceImpl extends BaseServiceImpl<PaymentCompanyQuote, Long> implements PaymentCompanyQuoteService {

    private final PaymentCompanyQuoteRepository paymentCompanyQuoteRepository;
    private final ThymeleafService thymeleafService;
    private final VatService vatService;
    private final ModelPriceHistoryRepository modelPriceHistoryRepository;
    private final PaymentCompanyQuoteMapper paymentCompanyQuoteMapper;
    private final AddressService addressService;

    public PaymentCompanyQuoteServiceImpl(BaseRepository<PaymentCompanyQuote, Long> repository,
                                          PaymentCompanyQuoteRepository paymentCompanyQuoteRepository,
                                          ThymeleafService thymeleafService, VatService vatService,
                                          ModelPriceHistoryRepository modelPriceHistoryRepository,
                                          PaymentCompanyQuoteMapper paymentCompanyQuoteMapper, AddressService addressService) {
        super(repository);
        this.paymentCompanyQuoteRepository = paymentCompanyQuoteRepository;
        this.thymeleafService = thymeleafService;
        this.vatService = vatService;
        this.modelPriceHistoryRepository = modelPriceHistoryRepository;
        this.paymentCompanyQuoteMapper = paymentCompanyQuoteMapper;
        this.addressService = addressService;
    }

    @Override
    @Transactional
    public Long create(PaymentCompanyQuoteDTO paymentCompanyQuoteDTO) {
        PaymentCompanyQuote PaymentCompanyQuote = new PaymentCompanyQuote();
        PaymentCompanyQuote.setQty(paymentCompanyQuoteDTO.getQty());
        PaymentCompanyQuote.setModelId(paymentCompanyQuoteDTO.getModelId());
        PaymentCompanyQuote.setCompanyId(paymentCompanyQuoteDTO.getCompanyId());

        PaymentCompanyQuote = paymentCompanyQuoteRepository.save(PaymentCompanyQuote);
        return PaymentCompanyQuote.getId();
    }


    @Override
    public PaymentCompanyQuoteDetailDTO get(Long id) {
        return paymentCompanyQuoteMapper.toDto(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentCompanyQuoteDetailDTO> list(BaseFilter filter) {
        Specification<PaymentCompanyQuote> specification = (root, criteriaQuery, builder) -> {
            criteriaQuery.distinct(true);

            Predicate and = builder.and(Stream.of(
                with(filter::getModelId, a -> builder.equal(root.get(PaymentCompanyQuote_.MODEL_ID), a))
            ).filter(Objects::nonNull).toArray(Predicate[]::new));

            Path<Company> companyPath = root.join(PaymentCompanyQuote_.COMPANY, JoinType.INNER);
            Predicate or = builder.or(Stream.of(
                with(filter::getSearch, a -> builder.like(builder.lower(companyPath.get(Company_.NAME)), a)),
                with(filter::getSearch, a -> builder.like(builder.lower(companyPath.get(Company_.TIN)), a))
            ).filter(Objects::nonNull).toArray(Predicate[]::new));
            return builder.or(and, or);
        };
        return paymentCompanyQuoteRepository.findAll(specification, filter.getOrderedPageable())
            .map(paymentCompanyQuoteMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentCompanyQuoteDetailDTO> paymentCompanyQuoteListByTin(String tin) {
        return paymentCompanyQuoteRepository.findAllByCompany_TinAndPaymentCompanyContractsIsNull(tin).stream().map(paymentCompanyQuoteMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public String generateHtmlTemplate(Long id) {
        PaymentCompanyQuote paymentCompanyQuote = findById(id);
        Company company = paymentCompanyQuote.getCompany();
        CompanyBank companyBank = paymentCompanyQuote.getCompany().getCompanyBank();
        PaymentCompanyQuoteTemplateDTO paymentCompanyQuoteTemplateDTO = new PaymentCompanyQuoteTemplateDTO();
        paymentCompanyQuoteTemplateDTO.setId(paymentCompanyQuote.getId());
        paymentCompanyQuoteTemplateDTO.setTin(company.getTin());
        paymentCompanyQuoteTemplateDTO.setCreatedDate(DateUtil.getLocalDateTimeAsStringWithoutSecond(paymentCompanyQuote.getCreatedDate()));
        paymentCompanyQuoteTemplateDTO.setCompanyName(company.getName());
        paymentCompanyQuoteTemplateDTO.setOked(companyBank.getOked());
        paymentCompanyQuoteTemplateDTO.setAccountNumber(companyBank.getAccountNumber());
        paymentCompanyQuoteTemplateDTO.setBankName(companyBank.getBank().getNameNotNullable());
        paymentCompanyQuoteTemplateDTO.setModelName(paymentCompanyQuote.getModel().getName());
        Optional.ofNullable(addressService.getLegalAddress(company.getEntityId(), company.getEntityType()))
                .ifPresent(address -> paymentCompanyQuoteTemplateDTO.setFullAddress(address.getFullAddressAddress()));
        paymentCompanyQuoteTemplateDTO.setQty(paymentCompanyQuote.getQty());

        ModelPriceHistory modelPriceHistory = modelPriceHistoryRepository.findTopByModelIdOrderByIdDesc(paymentCompanyQuote.getModelId());
        VatInvoiceCalculationDto calculationVatDto = CalcUtils.calculateInvoice(modelPriceHistory.getPrice(), paymentCompanyQuote.getQty(), vatService.getActiveVat().getPercent());

        paymentCompanyQuoteTemplateDTO.setPriceWithVat(calculationVatDto.getPriceWithVat());
        paymentCompanyQuoteTemplateDTO.setPrice(calculationVatDto.getPrice());
        paymentCompanyQuoteTemplateDTO.setTotalPrice(calculationVatDto.getTotalPrice());
        paymentCompanyQuoteTemplateDTO.setTotalPriceWithVat(calculationVatDto.getTotalPriceWithVat());

        return thymeleafService.generateContractPaymentQuoteHTML(paymentCompanyQuoteTemplateDTO);
    }
}
