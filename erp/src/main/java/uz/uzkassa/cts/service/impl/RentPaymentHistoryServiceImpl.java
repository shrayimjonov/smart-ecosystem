package uz.uzkassa.cts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import uz.uzkassa.cts.domain.RentActAccessory;
import uz.uzkassa.cts.domain.RentPaymentHistory;
import uz.uzkassa.cts.domain.RentPaymentHistoryId;
import uz.uzkassa.cts.dto.rentpaymenthistory.request.RentPaymentHistoryRequestDto;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqRentProducer;
import uz.uzkassa.cts.repository.RentActAccessoryRepository;
import uz.uzkassa.cts.repository.RentPaymentHistoryRepository;
import uz.uzkassa.cts.service.RentPaymentHistoryService;
import uz.uzkassa.cts.utils.BigDecimalUtil;
import uz.uzkassa.cts.utils.DateUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RentPaymentHistoryServiceImpl extends BaseServiceImpl<RentPaymentHistory, RentPaymentHistoryId> implements RentPaymentHistoryService {

    private final RentPaymentHistoryRepository repository;
    private final RentActAccessoryRepository rentActAccessoryRepository;
    private final RabbitMqRentProducer rentProducer;


    public RentPaymentHistoryServiceImpl(RentPaymentHistoryRepository repository,
                                         RentActAccessoryRepository rentActAccessoryRepository,
                                         RabbitMqRentProducer rentProducer) {
        super(repository);
        this.repository = repository;
        this.rentActAccessoryRepository = rentActAccessoryRepository;
        this.rentProducer = rentProducer;
    }


    @Override
    public void createHistoryByCustomerId(Long customerId) {
        List<RentActAccessory> accessories = rentActAccessoryRepository.findByActiveAndCustomerId(true, customerId);
        List<RentPaymentHistory> histories = new ArrayList<>();
        boolean changed = false;
        for (RentActAccessory accessory : accessories) {
            RentPaymentHistory history = new RentPaymentHistory();
            BigDecimal calculatedAmount =
                BigDecimalUtil.calculatePrice(accessory.getStartDate(),
                    accessory.getDueDate(),
                    accessory.getAccessory().getRentPrice());
            history.setAmount(BigDecimalUtil.setScaleByTwo(calculatedAmount));
            history.setId(new RentPaymentHistoryId(
                accessory.getCustomerId(),
                accessory.getDeviceSerialNumber(),
                accessory.getStartDate().getMonth(),
                accessory.getStartDate().getYear()
            ));
            history.setDueDate(accessory.getDueDate());
            history.setRentActAccessoryId(accessory.getId());
            history.setCustomer(accessory.getCustomer());
            history.setOfferId(accessory.getAct().getOfferId());
            history.setStartDate(accessory.getStartDate());
            histories.add(history);

            if (LocalDate.now().isEqual(accessory.getDueDate()) ||
                LocalDate.now().isAfter(accessory.getDueDate())) {
                accessory.setStartDate(accessory.getDueDate());
                accessory.setDueDate(DateUtil.calculateDueDate(accessory.getDueDate()));
                changed = true;
            }
        }
        if (changed)
            rentActAccessoryRepository.saveAll(accessories);

        List<RentPaymentHistory> historiesToSave = new ArrayList<>();
        List<RentPaymentHistory> historiesFromDb = repository.findByIdIn(histories.stream().map(RentPaymentHistory::getId).collect(Collectors.toList()));
        for (RentPaymentHistory history : histories) {
            if (!historiesFromDb.contains(history)) {
                historiesToSave.add(history);
            }
        }

        saveAll(historiesToSave);
    }

    @Override
    public Page<RentPaymentHistoryRequestDto> getByCustomerId(BaseFilter filter) {
        return repository.findByCustomerIdAndInvoiceIdIsNull(filter.getCustomerId(), filter.getOrderedPageable())
            .map(this::mapToDto);
    }

    @Override
    public List<RentPaymentHistoryRequestDto> getByOfferId(Long offerId) {
        return repository.findByOfferId(offerId)
            .stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<RentPaymentHistoryRequestDto> getCurrentDetailsByOfferId(Long offerId) {
        return repository.findByOfferIdAndInvoiceIdIsNull(offerId)
            .stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<RentPaymentHistoryRequestDto> getByInvoiceId(Long invoiceId) {
        return repository.findByInvoiceId(invoiceId)
            .stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
    }


    private RentPaymentHistoryRequestDto mapToDto(RentPaymentHistory m) {
        RentPaymentHistoryRequestDto dto = new RentPaymentHistoryRequestDto();
        dto.setCustomerId(m.getId().getCustomerId());
        dto.setCustomerName(m.getCustomer().getName());
        dto.setAmount(m.getAmount());
        dto.setStartDate(m.getStartDate());
        dto.setDueDate(m.getDueDate());
        dto.setDeviceSerialNumber(m.getId().getDeviceSerialNumber());
        dto.setMonth(m.getId().getMonth());
        dto.setYear(m.getId().getYear());
        dto.setOfferId(m.getOfferId());
        return dto;
    }

    public List<RentPaymentHistory> getListForBonusCalculation() {
        LocalDate now = LocalDate.now();
        LocalDate accCreatedDate = DateUtil.getMonthFirstDate(now.minusDays(2));
        LocalDate hisCreatedDate = DateUtil.getMonthFirstDate(now);
        return repository.findBonusCalcList(accCreatedDate.atStartOfDay(), hisCreatedDate.atStartOfDay());
    }

    @Override
    public void createPaymentHistoryCron() {
        log.info(">>>>>>>>>> Start Cron Create Rent history <<<<<<<<<<<<");
        rentActAccessoryRepository.findByActiveActOfReturnIdIsNull().forEach(rentProducer::createRentPaymentHistory);
        log.info(">>>>>>>>>> End Cron Create Rent history <<<<<<<<<<<<");
    }

    @Override
    public void calculateBonusCron() {
        LocalDate now = LocalDate.now();
        LocalDate accCreatedDate = DateUtil.getMonthFirstDate(now.minusMonths(2));
        LocalDate hisCreatedDate = DateUtil.getMonthFirstDate(now);
        List<RentPaymentHistory> histories = repository.findBonusCalcList(accCreatedDate.atStartOfDay(), hisCreatedDate.atStartOfDay());
        Set<RentPaymentHistory> histForUpdate = new HashSet<>();
        for (RentPaymentHistory history : histories) {
            history.setBonus(history.getRentActAccessory().getAccessory().getBonus());
            histForUpdate.add(history);
        }
        repository.saveAll(histForUpdate);
    }
}
