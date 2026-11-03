package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.dto.category.EDocsGenerateDTO;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.FacturaType;
import uz.uzkassa.smartpossupply.enums.GenericSettingsFieldType;
import uz.uzkassa.smartpossupply.enums.GenericSettingsType;
import uz.uzkassa.smartpossupply.rabbitmq.producer.RabbitMqGenerateEDocumentProducer;
import uz.uzkassa.smartpossupply.repository.StockBaseRepository;

import java.util.List;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EDocService extends BaseService {

    FacturaService facturaService;

    WaybillService waybillService;

    AcceptanceTransferActService acceptanceTransferActService;

    RabbitMqGenerateEDocumentProducer rabbitMqGenerateEDocumentProducer;
    StockBaseRepository stockBaseRepository;

    @Transactional
    public void generateEDocs(EDocsGenerateDTO payload) {
        switch (payload.getDocType()) {
            case FACTURA -> {
                if (EntityTypeEnum.STOCK_OUT == payload.getEntityType()) {
                    facturaService.convertFromStockV2(payload.getEntityId(), null, FacturaType.STANDART.getId(), payload.getCompanyId());
                } else if (EntityTypeEnum.STOCK_RETURN == payload.getEntityType()) {
                    facturaService.convertFromStock(stockBaseRepository.getReferenceById(payload.getEntityId()), null, FacturaType.ADDITIONAL.getId(), payload.getCompanyId());
                }
            }
            case WAY_BILL -> {
                if (EntityTypeEnum.STOCK_OUT == payload.getEntityType()) {
                    waybillService.convertFromStockOut(payload.getEntityId());
                } else if (EntityTypeEnum.STOCK_TRANSFER == payload.getEntityType()) {
                    waybillService.convertFromStockTransfer(payload.getEntityId());
                }
            }
            case ACCEPTANCE_TRANSFER_ACT -> {
                if (EntityTypeEnum.STOCK_OUT == payload.getEntityType()) {
                    acceptanceTransferActService.createByStockOut(payload.getEntityId());
                }
            }
        }
    }

    @Transactional
    public void generateDoc(EDocsGenerateDTO payload) {
        companySettingsRepository.findFirstByCompanyIdAndSettings_SettingsType(getCurrentCompanyId(), GenericSettingsType.E_DOCUMENTS)
                .ifPresent(companySettings -> {
                    if (companySettings.isEnabled()) {
                        Map<GenericSettingsFieldType, Object> map = companySettings.getValues();
                        List<String> list = (List<String>) map.get(GenericSettingsFieldType.GENERATE_DOCS_AUTOMATICALLY);
                        list.forEach((fieldType) -> {
                            if (GenericSettingsFieldType.FACTURA == GenericSettingsFieldType.valueOf(fieldType)) {
                                rabbitMqGenerateEDocumentProducer.sendToQueue(new EDocsGenerateDTO(payload.getEntityId(), payload.getEntityType(), EntityTypeEnum.FACTURA, payload.getCompanyId()));
                            }
                            if (GenericSettingsFieldType.WAYBILL == GenericSettingsFieldType.valueOf(fieldType)) {
                                rabbitMqGenerateEDocumentProducer.sendToQueue(new EDocsGenerateDTO(payload.getEntityId(), payload.getEntityType(), EntityTypeEnum.WAY_BILL, payload.getCompanyId()));
                            }
                            if (GenericSettingsFieldType.ACCEPTANCE_TRANSFER_ACT == GenericSettingsFieldType.valueOf(fieldType)) {
                                rabbitMqGenerateEDocumentProducer.sendToQueue(new EDocsGenerateDTO(payload.getEntityId(), payload.getEntityType(), EntityTypeEnum.ACCEPTANCE_TRANSFER_ACT, payload.getCompanyId()));
                            }
                        });
                    }

                });
    }
}
