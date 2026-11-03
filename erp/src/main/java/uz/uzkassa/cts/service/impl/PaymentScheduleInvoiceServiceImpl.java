package uz.uzkassa.cts.service.impl;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.dto.invoice.request.LedgerInvoiceRequestDto;
import uz.uzkassa.cts.dto.paymentscheduleinvoice.response.PaymentScheduleInvoiceResponseDto;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.ServiceType;
import uz.uzkassa.cts.filter.PaymentScheduleInvoiceFilter;
import uz.uzkassa.cts.mappings.PaymentScheduleInvoiceMapper;
import uz.uzkassa.cts.repository.PaymentScheduleInvoiceRepository;
import uz.uzkassa.cts.service.*;
import uz.uzkassa.cts.utils.BigDecimalUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PaymentScheduleInvoiceServiceImpl extends BaseServiceImpl<PaymentScheduleInvoice, Long> implements PaymentScheduleInvoiceService {

    private final PaymentScheduleInvoiceRepository repository;
    private final LedgerService ledgerService;
    private final VatService vatService;
    private final ActPaymentScheduleService actPaymentScheduleService;
    private final ActOfTransmissionStrategyService actOfTransmissionService;
    private final ThymeleafService thymeleafService;
    private final PaymentScheduleInvoiceMapper paymentScheduleInvoiceMapper;

    public PaymentScheduleInvoiceServiceImpl(PaymentScheduleInvoiceRepository repository,
                                             LedgerService ledgerService,
                                             VatService vatService,
                                             ActPaymentScheduleService actPaymentScheduleService,
                                             ActOfTransmissionStrategyService actOfTransmissionService,
                                             ThymeleafService thymeleafService,
                                             PaymentScheduleInvoiceMapper paymentScheduleInvoiceMapper) {
        super(repository);
        this.repository = repository;
        this.ledgerService = ledgerService;
        this.vatService = vatService;
        this.actPaymentScheduleService = actPaymentScheduleService;
        this.actOfTransmissionService = actOfTransmissionService;
        this.thymeleafService = thymeleafService;
        this.paymentScheduleInvoiceMapper = paymentScheduleInvoiceMapper;
    }

    @Override
    public PaymentScheduleInvoice create(ActPaymentSchedule schedule, ContractForLeasing contract) {
        PaymentScheduleInvoice invoice = new PaymentScheduleInvoice();

        Vat vat = vatService.getActiveVat();
        BigDecimal totalPriceWithVat = schedule.getAmount();
        BigDecimal price = totalPriceWithVat.divide(BigDecimal.valueOf(schedule.getTotalProductCount()), RoundingMode.HALF_UP);
        BigDecimal vatPriceFor1 = BigDecimalUtil.calculateVatPriceFor1(price, vat.getPercent());
        BigDecimal priceFor1pc = BigDecimalUtil.calculateProductPriceFor1SubtractVatPrice(price, vatPriceFor1);
        BigDecimal totalVatPrice = vatPriceFor1.multiply(BigDecimal.valueOf(schedule.getTotalProductCount()));

//        ActOfTransmission act = schedule.getActOfTransmission();
        ActOfTransmissionDetail detail = schedule.getActOfTransmissionDetail();
        PaymentScheduleInvoiceDetail invoiceDetail = new PaymentScheduleInvoiceDetail();
        invoiceDetail.setDeviceId(detail.getDeviceId());
        invoiceDetail.setPriceWithVatFor1pc(price);
        invoiceDetail.setPriceFor1pc(priceFor1pc);
        invoiceDetail.setVatFor1pc(vatPriceFor1);
        invoice.addDetail(invoiceDetail);


        invoice.setTotalProductCount(schedule.getTotalProductCount());
        invoice.setTotalSellPrice(totalPriceWithVat);
        BigDecimal totalPrice = totalPriceWithVat.subtract(totalVatPrice);
        invoice.setTotalPrice(totalPrice);
        invoice.setTotalVatPrice(totalVatPrice);
        invoice.setContractId(contract.getId());
        invoice.setServiceName(contract.getModelPriceHistory().getDeviceModel().getName());
        invoice.setStatus(ContractStatus.ACCEPTED);
        invoice = save(invoice);
        schedule.setInvoiceId(invoice.getId());
        actPaymentScheduleService.save(schedule);
        return invoice;
    }

    @Override
    public void changeActStatus() {
        for (ActOfTransmission act : repository.changeActStatus()) {
            act.setStatus(ContractStatus.CLOSED);
            actOfTransmissionService.save(act);
        }
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createInvoiceAndChangeBalance(ActPaymentSchedule schedule) {
//        ActOfTransmission act = schedule.getActOfTransmission();
        ActOfTransmissionDetail detail = schedule.getActOfTransmissionDetail();
        ActOfTransmission act = detail.getActOfTransmission();
        ContractForLeasing contract = act.getContract();
        PaymentScheduleInvoice invoice = create(schedule, contract);

        Company company = contract.getCompany();
        Customer customer = contract.getCustomer();
        LedgerInvoiceRequestDto requestDto = new LedgerInvoiceRequestDto()
                .id(invoice.getId())
                .documentNo(invoice.getNumber())
                .transactionNumber(invoice.getId().toString())
                .contractNumber(contract.getNumber())
                .serviceType(ServiceType.SERVICE)
                .amount(schedule.getAmount())
                .paymentDate(invoice.getCreatedDate().toLocalDate())
                .company(company)
                .customer(customer)
                .link("/api/payment-schedule-invoices/" + invoice.getId() + "/generateTemplate")
                .description(invoice.createDescription(act.getNumber(), act.getCreatedDate().toLocalDate(),
                        contract, schedule.getAmount()));

        ledgerService.payForOrder(requestDto);
    }

    @Override
    public List<PaymentScheduleInvoiceResponseDto> getByActOfTransmissionId(Long id) {
//        List<PaymentScheduleInvoice> invoices = repository.findBySchedule_ActOfTransmission_IdOrderByIdDesc(id);
        //  @todo must get by act detail ID
        List<PaymentScheduleInvoice> invoices = repository.findBySchedule_ActOfTransmissionDetail_IdOrderByIdDesc(id);
        return invoices.stream().map(paymentScheduleInvoiceMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public Page<PaymentScheduleInvoiceResponseDto> getList(PaymentScheduleInvoiceFilter filter) {
        Specification<PaymentScheduleInvoice> specification = (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);

            ArrayList<Predicate> list = new ArrayList<>();
            if (StringUtils.isNotEmpty(filter.getSearch())) {
                Path<String> id = root.get(PaymentScheduleInvoice_.ID);
                Path<ContractForLeasing> contractPath = root.join(PaymentScheduleInvoice_.CONTRACT);
                Path<Customer> customer = contractPath.get(ContractForLeasing_.CUSTOMER);
                Path<String> tin = customer.get(Customer_.TIN);
                Predicate tinPred = criteriaBuilder.like(criteriaBuilder.lower(tin), filter.getSearchForQuery());
                Predicate idPred = criteriaBuilder.like(id, filter.getSearchForQuery());
                list.add(criteriaBuilder.or(idPred, tinPred));
            }
            if (filter.getFrom() != null && filter.getTo() != null) {
                Path<LocalDateTime> createDate = root.get(PaymentScheduleInvoice_.CREATED_DATE);
                list.add(criteriaBuilder.between(createDate, filter.getFrom().atStartOfDay(), filter.getTo().atTime(LocalTime.MAX)));
            }
            if (filter.getStatus() != null) {
                Path<ContractStatus> status = root.get(PaymentScheduleInvoice_.STATUS);
                list.add(criteriaBuilder.equal(status, ContractStatus.valueOf(filter.getStatus())));
            }
            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
        Page<PaymentScheduleInvoice> actOfTransmissions = repository.findAll(specification, filter.getOrderedPageable());
        return actOfTransmissions.map(paymentScheduleInvoiceMapper::toDto);
    }

    @Override
    public String getTemplateById(Long id) {
        return thymeleafService.generatePaymentScheduleInvoice(findById(id));
    }

    @Override
    public PaymentScheduleInvoiceResponseDto getById(Long id) {
        return paymentScheduleInvoiceMapper.toDto(findById(id));
    }

    @Override
    public PaymentScheduleInvoiceResponseDto getByScheduleId(Long id) {
        PaymentScheduleInvoice invoice = repository.findBySchedule_id(id)
                .orElseThrow(validationService.entityNotFoundExceptionThrow(INVOICE));
        return paymentScheduleInvoiceMapper.toDto(invoice);
    }

    @Override
    public String getTemplateByScheduleId(Long id) {
        PaymentScheduleInvoice invoice = repository.findBySchedule_id(id)
                .orElseThrow(validationService.entityNotFoundExceptionThrow(INVOICE));
        return thymeleafService.generatePaymentScheduleInvoice(invoice);
    }
}
