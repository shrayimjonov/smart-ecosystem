package uz.uzkassa.cts.service.impl;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.uzkassa.cts.domain.BankClientDetail;
import uz.uzkassa.cts.domain.BankClientJournal;
import uz.uzkassa.cts.domain.KapitalLegalPaymentDetail;
import uz.uzkassa.cts.domain.KapitalLegalPaymentDetail_;
import uz.uzkassa.cts.dto.bankclient.request.BankClientDto;
import uz.uzkassa.cts.dto.kapitallegal.response.KapitalLegalResponseDto;
import uz.uzkassa.cts.enums.ServiceType;
import uz.uzkassa.cts.enums.kapitalbank.KapitalLegalDirectionType;
import uz.uzkassa.cts.enums.kapitalbank.KapitalLegalDocumentState;
import uz.uzkassa.cts.enums.kapitalbank.KapitalLegalDocumentType;
import uz.uzkassa.cts.filter.KapitalLegalFilter;
import uz.uzkassa.cts.mappings.KapitalLegalPaymentDetailMapper;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqDeviceSwitcherProducer;
import uz.uzkassa.cts.repository.KapitalLegalPaymentDetailRepository;
import uz.uzkassa.cts.service.BankClientService;
import uz.uzkassa.cts.service.KapitalLegalPaymentDetailService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class KapitalLegalPaymentDetailServiceImpl extends BaseServiceImpl<KapitalLegalPaymentDetail, Long> implements KapitalLegalPaymentDetailService {

    private final KapitalLegalPaymentDetailRepository repository;
    private final BankClientService bankClientService;
    private final RabbitMqDeviceSwitcherProducer rabbitMqProducer;
    private final KapitalLegalPaymentDetailMapper kapitalLegalPaymentDetailMapper;

    public KapitalLegalPaymentDetailServiceImpl(KapitalLegalPaymentDetailRepository repository,
                                                BankClientService bankClientService,
                                                RabbitMqDeviceSwitcherProducer rabbitMqProducer,
                                                KapitalLegalPaymentDetailMapper kapitalLegalPaymentDetailMapper) {
        super(repository);
        this.repository = repository;
        this.bankClientService = bankClientService;
        this.rabbitMqProducer = rabbitMqProducer;
        this.kapitalLegalPaymentDetailMapper = kapitalLegalPaymentDetailMapper;
    }


    @Override
    public void sentToBillingCronJob() {
        List<KapitalLegalDocumentType> documentTypes = new ArrayList<>();
        documentTypes.add(KapitalLegalDocumentType.PAYMENT_24_7_VIA_ANOR);
        documentTypes.add(KapitalLegalDocumentType.PAYMENT_ORDER);
        List<KapitalLegalPaymentDetail> details =
            repository.findAllBySentToBillingAndStateAndDirectionTypeAndDocumentTypeInOrderByIdAsc(
                false,
                KapitalLegalDocumentState.HELD,
                KapitalLegalDirectionType.INCOME,
                documentTypes);
        if (details.isEmpty())
            return;

        sendToBillingForApprove(details);
        deviceSwitch(details);
    }

    private BankClientJournal getJournal() {
        BankClientJournal journal = bankClientService.findTopJournal();
        Month month = LocalDate.now().getMonth();
        if (!journal.getCreatedDate().getMonth().equals(month)) {
            BankClientDto dto = new BankClientDto();
            dto.setName(month.toString());
            dto.setServiceType(ServiceType.SERVICE);
            bankClientService.create(dto);
            journal = bankClientService.findTopJournal();
        }
        return journal;
    }

    @Override
    public Page<KapitalLegalResponseDto> list(KapitalLegalFilter filter) {
        Specification<KapitalLegalPaymentDetail> specification = (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);
            ArrayList<Predicate> list = getPredicates(filter, criteriaBuilder, root);
            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
        Page<KapitalLegalPaymentDetail> details = repository.findAll(specification, filter.getOrderedPageable());

        return details.map(kapitalLegalPaymentDetailMapper::toDto);
    }


    @Override
    public BigDecimal getAmount(KapitalLegalFilter filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<BigDecimal> query = cb.createQuery(BigDecimal.class);
        query.distinct(true);
        Root<KapitalLegalPaymentDetail> root = query.from(KapitalLegalPaymentDetail.class);

        ArrayList<Predicate> list = getPredicates(filter, cb, root);

        query.where(cb.and(list.toArray(new Predicate[0])));
        query.select(cb.sum(root.get(KapitalLegalPaymentDetail_.AMOUNT).as(BigDecimal.class)));
        TypedQuery<BigDecimal> typedQuery = entityManager.createQuery(query);

        return typedQuery.getSingleResult().divide(BigDecimal.valueOf(100), RoundingMode.CEILING);
    }

    private ArrayList<Predicate> getPredicates(KapitalLegalFilter filter, CriteriaBuilder cb, Root<KapitalLegalPaymentDetail> root) {
        ArrayList<Predicate> list = new ArrayList<>();
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            list.add(cb.or(
                cb.like(root.get(KapitalLegalPaymentDetail_.ACCOUNT_DEBIT), filter.getSearchForQuery()),
                cb.like(root.get(KapitalLegalPaymentDetail_.TIN_DEBIT), filter.getSearchForQuery()),
                cb.like(root.get(KapitalLegalPaymentDetail_.MFO_DEBIT), filter.getSearchForQuery()),
                cb.like(cb.lower(root.get(KapitalLegalPaymentDetail_.NAME_DEBIT)), filter.getSearchForQuery()),
                cb.like(cb.lower(root.get(KapitalLegalPaymentDetail_.DOCUMENT_NUMBER)), filter.getSearchForQuery()),
                cb.like(cb.lower(root.get(KapitalLegalPaymentDetail_.PURPOSE)), filter.getSearchForQuery())));
        }
        if (filter.getDirectionType() != null) {
            list.add(cb.equal(root.get(KapitalLegalPaymentDetail_.DIRECTION_TYPE), filter.getDirectionType()));
        }
        if (filter.getDocumentType() != null) {
            list.add(cb.equal(root.get(KapitalLegalPaymentDetail_.DOCUMENT_TYPE), filter.getDocumentType()));
        }
        if (filter.getState() != null) {
            list.add(cb.equal(root.get(KapitalLegalPaymentDetail_.STATE), filter.getState()));
        }
        if (filter.getSentToBilling() != null) {
            list.add(cb.equal(root.get(KapitalLegalPaymentDetail_.SENT_TO_BILLING), filter.getSentToBilling()));
        }
        if (filter.getFrom() != null && filter.getTo() != null) {
            Expression<Date> dateFieldAsDate = cb.function("TO_DATE", Date.class,
                root.get(KapitalLegalPaymentDetail_.DOCUMENT_DATE),
                cb.literal("DD.MM.YYYY"));
            ZoneId defaultZoneId = ZoneId.systemDefault();
            list.add(cb.between(dateFieldAsDate,
                Date.from(filter.getFrom().atStartOfDay(defaultZoneId).toInstant()),
                Date.from(filter.getTo().atStartOfDay(defaultZoneId).toInstant())));
        }
        list.add(cb.notEqual(root.get(KapitalLegalPaymentDetail_.TIN_DEBIT), "306351564"));
        return list;
    }

    @Override
    public void sendToBilling(List<Long> ids) {
        List<KapitalLegalPaymentDetail> details =
            repository.findAllBySentToBillingAndStateAndDirectionTypeAndIdInOrderByIdAsc(
                false,
                KapitalLegalDocumentState.HELD,
                KapitalLegalDirectionType.INCOME,
                ids);

        if (details.isEmpty())
            return;

        sendToBillingForApprove(details);
        deviceSwitch(details);
    }

    private void deviceSwitch(List<KapitalLegalPaymentDetail> details) {
        for (KapitalLegalPaymentDetail detail : details) {
            rabbitMqProducer.deviceStatusSwitch(detail.getTinDebit());
        }
    }

    private void sendToBillingForApprove(List<KapitalLegalPaymentDetail> details) {
        BankClientJournal journal = getJournal();
        List<BankClientDetail> detailsToSave = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        for (KapitalLegalPaymentDetail paymentDetail : details) {
            BankClientDetail detail = new BankClientDetail();
            try {
                detail.setPaymentDate(LocalDate.parse(paymentDetail.getDocumentDate(), formatter));
            } catch (Exception ex) {
                continue;
            }
            detail.setPayerAccountNumber(paymentDetail.getAccountDebit());
            detail.setPayerName(paymentDetail.getNameDebit());
            detail.setPayerBankCode(paymentDetail.getMfoDebit());
            detail.setPayerBankName("");
            long zero = StringUtils.isNotEmpty(paymentDetail.getTinDebit()) ? Long.parseLong(paymentDetail.getTinDebit()) : 0;
            if (zero == 0)
                detail.setPayerIdentityNumber("000000000");
            else
                detail.setPayerIdentityNumber(paymentDetail.getTinDebit());

            detail.setTransactionNumber(paymentDetail.getId().toString());
            detail.setDocumentNo(paymentDetail.getDocumentNumber());
            detail.setAmount(fromStringTiyinToBigDecimal(paymentDetail.getAmount()));
            detail.setDescription(paymentDetail.getPurpose());
            detail.setServiceType(ServiceType.SERVICE);

            detail.setReceiverIdentityNumber("BANK_SRVC");
            detail.setReceiverAccountNumber("20208000905065950001");
            detail.setReceiverName("ООО \"SPECIAL TEXNO SOFT\"");
            detail.setReceiverBankCode("01088");
            detail.setReceiverBankName("Головной Офис");
            detail.setBankClientJournal(journal);

//            journal.addBankClientDetail(detail);
            detailsToSave.add(detail);
            paymentDetail.setSentToBilling(true);
        }

        bankClientService.insertIntoBankClientDetails(journal, detailsToSave);
        bankClientService.approveDetails(journal.getId());

        repository.saveAll(details);
    }

    private BigDecimal fromStringTiyinToBigDecimal(String amount) {
        if (StringUtils.isEmpty(amount))
            return BigDecimal.ZERO;
        return new BigDecimal(amount).divide(BigDecimal.valueOf(100));
    }
}
