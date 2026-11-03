package uz.uzkassa.cts.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.BankTariff;
import uz.uzkassa.cts.domain.BankTariffHistory;
import uz.uzkassa.cts.dto.banktariff.BankTariffHistoryDTO;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.payload.Payload;
import uz.uzkassa.cts.enums.BankTariffHistoryType;
import uz.uzkassa.cts.integrations.SmartposClient;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqBankTariffProducer;
import uz.uzkassa.cts.repository.BankTariffHistoryRepository;
import uz.uzkassa.cts.service.BankTariffHistoryService;

import java.time.LocalDateTime;

@Transactional
@Service
public class BankTariffHistoryServiceImpl extends BaseServiceImpl<BankTariffHistory, Long> implements BankTariffHistoryService {

    private final BankTariffHistoryRepository bankTariffHistoryRepository;
    private final RabbitMqBankTariffProducer rabbitMqBankTariffProducer;
    private final SmartposClient smartposClient;

    public BankTariffHistoryServiceImpl(BankTariffHistoryRepository bankTariffHistoryRepository,
                                        RabbitMqBankTariffProducer rabbitMqBankTariffProducer,
                                        SmartposClient smartposClient) {
        super(bankTariffHistoryRepository);
        this.bankTariffHistoryRepository = bankTariffHistoryRepository;
        this.rabbitMqBankTariffProducer = rabbitMqBankTariffProducer;
        this.smartposClient = smartposClient;
    }

    @Override
    public void create(Long customerId, Long bankTariffId) {
        BankTariffHistory bankTariffHistory = new BankTariffHistory();
        bankTariffHistory.setCustomerId(customerId);
        bankTariffHistory.setBankTariffId(bankTariffId);
        bankTariffHistory.setCreatedDate(LocalDateTime.now());
        bankTariffHistory.setStatus(BankTariffHistoryType.WAIT);
        bankTariffHistoryRepository.save(bankTariffHistory);
    }

    @Override
    public void syncWithSmartPos() {
        bankTariffHistoryRepository.findAllByStatus(BankTariffHistoryType.WAIT).forEach(history -> {
            rabbitMqBankTariffProducer.sync(new Payload(history.getId()));
        });
    }

    @Override
    public void syncListener(Payload payload) {
        BankTariffHistory bankTariffHistory = findById(payload.getId());
        BankTariff bankTariff = bankTariffHistory.getBankTariff();
        BankTariffHistoryDTO bankTariffHistoryDTO = new BankTariffHistoryDTO();
        bankTariffHistoryDTO.setTin(bankTariffHistory.getCustomer().getTin());
        bankTariffHistoryDTO.setOldTin(bankTariffHistory.getCustomer().getOldTin());
        bankTariffHistoryDTO.setCustomerId(bankTariffHistory.getCustomerId());
        bankTariffHistoryDTO.setDate(bankTariffHistory.getCreatedDate());
        bankTariffHistoryDTO.setBankType(bankTariff.getBankType().getCode());
        bankTariffHistoryDTO.setHumoPercentage(bankTariff.getHumoPercentage());
        bankTariffHistoryDTO.setVisaPercentage(bankTariff.getVisaPercentage());
        bankTariffHistoryDTO.setVisaDomesticPercentage(bankTariff.getVisaDomesticPercentage());
        bankTariffHistoryDTO.setUzcardPercentage(bankTariff.getUzcardPercentage());
        bankTariffHistoryDTO.setXizmat(new CommonDTO(bankTariff.getXizmatId(), bankTariff.getXizmatName()));
        smartposClient.bankTariffHistory(bankTariffHistoryDTO);

        bankTariffHistory.setStatus(BankTariffHistoryType.SENT);
        save(bankTariffHistory);
    }
}
