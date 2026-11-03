package uz.uzkassa.cts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.FiscalModule;
import uz.uzkassa.cts.domain.InactiveFiscalModule;
import uz.uzkassa.cts.dto.payload.OfdSyncInactiveFiscalModulePayload;
import uz.uzkassa.cts.dto.tin.integration.OfdInactiveFiscalModuleResponseDTO;
import uz.uzkassa.cts.integrations.OfdClient;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqFiscalModuleProducer;
import uz.uzkassa.cts.repository.FiscalModuleRepository;
import uz.uzkassa.cts.repository.InactiveFiscalModuleRepository;
import uz.uzkassa.cts.service.InactiveFiscalModuleService;

import java.util.HashMap;
import java.util.Map;

@Transactional
@Service
@Slf4j
public class InactiveFiscalModuleServiceImpl extends BaseServiceImpl<InactiveFiscalModule, Long> implements InactiveFiscalModuleService {

    private final FiscalModuleRepository fiscalModuleRepository;
    private final InactiveFiscalModuleRepository inactiveFiscalModuleRepository;
    private final OfdClient ofdClient;
    private final RabbitMqFiscalModuleProducer rabbitMqFiscalModuleProducer;


    public InactiveFiscalModuleServiceImpl(InactiveFiscalModuleRepository inactiveFiscalModuleRepository,
                                           FiscalModuleRepository fiscalModuleRepository, OfdClient ofdClient, RabbitMqFiscalModuleProducer rabbitMqFiscalModuleProducer) {
        super(inactiveFiscalModuleRepository);
        this.inactiveFiscalModuleRepository = inactiveFiscalModuleRepository;
        this.fiscalModuleRepository = fiscalModuleRepository;
        this.ofdClient = ofdClient;
        this.rabbitMqFiscalModuleProducer = rabbitMqFiscalModuleProducer;
    }

    @Override
    public void sync() {
        OfdInactiveFiscalModuleResponseDTO ofdInactiveFiscalModuleResponseDTO = ofdClient.getInactiveFiscalModules();
        if (CollectionUtils.isNotEmpty(ofdInactiveFiscalModuleResponseDTO.getData())) {
            //Key  = TerminalId = Fiscal Module Serial
            //Value  = InactiveFiscalModule Object
            Map<String, InactiveFiscalModule> oldInactiveFiscalModules = new HashMap<>();
            inactiveFiscalModuleRepository
                .findAll()
                .forEach(inactiveFiscalModule -> oldInactiveFiscalModules.put(inactiveFiscalModule.getFiscalModule().getSerialNumber(), inactiveFiscalModule));

            ofdInactiveFiscalModuleResponseDTO.getData().forEach(ofdInactiveFiscalModuleDTO -> {
                rabbitMqFiscalModuleProducer.syncInactiveFiscalModules(ofdInactiveFiscalModuleDTO.getTerminalId());

                InactiveFiscalModule inactiveFiscalModule = oldInactiveFiscalModules.get(ofdInactiveFiscalModuleDTO.getTerminalId());
                if (inactiveFiscalModule != null) {
                    oldInactiveFiscalModules.remove(ofdInactiveFiscalModuleDTO.getTerminalId());
                }
            });

            if (!oldInactiveFiscalModules.isEmpty()) {
                inactiveFiscalModuleRepository.deleteAllInBatch(oldInactiveFiscalModules.values());
            }
        }
    }

    @Override
    public void syncInactiveFiscalModules(OfdSyncInactiveFiscalModulePayload ofdSyncInactiveFiscalModulePayload) {
        FiscalModule fiscalModule = fiscalModuleRepository.findBySerialNumber(ofdSyncInactiveFiscalModulePayload.getTerminalId());
        InactiveFiscalModule inactiveFiscalModule = inactiveFiscalModuleRepository.findFirstByFiscalModuleId(fiscalModule.getId()).orElse(new InactiveFiscalModule());
        if (inactiveFiscalModule.getId() == null) {
            inactiveFiscalModule.setFiscalModuleId(fiscalModule.getId());
            inactiveFiscalModuleRepository.save(inactiveFiscalModule);
        }
    }
}
