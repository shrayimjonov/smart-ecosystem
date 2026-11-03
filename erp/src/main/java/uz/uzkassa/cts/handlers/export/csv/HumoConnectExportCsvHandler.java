package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.filter.HumoConnectFilter;
import uz.uzkassa.cts.repository.HumoConnectRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class HumoConnectExportCsvHandler extends AbstractExportCsvHandler<HumoConnectFilter> {

    HumoConnectRepository humoConnectRepository;

    @Override
    public String getFileName(HumoConnectFilter filter) {
        return "Humo_Connect.csv";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Филиал", "Статус", "Статус подключения", "Адрес", "МФО", "Расчетный счет", "ФС", "Тип банка", "Платежная система", "Компания", "ИНН", "Серийный номер ККМ", "Сотрудник", "Тип кейса", "Лицензия", "Статус документа", "Первый чек", "Последний чек");
    }

    @Override
    protected void drawItemList(HumoConnectFilter filter, CSVWriter csvWriter) {
        humoConnectRepository
                .getResultList(filter)
                .getList()
                .forEach(humoConnect ->
                        csvWriter.writeNext(
                                new String[]{
                                        humoConnect.getCreatorBranch(),
                                        humoConnect.getStatus().getName(),
                                        humoConnect.getOperatorStatus().getName(),
                                        humoConnect.getAddress().getStreet(),
                                        humoConnect.getMfo(),
                                        humoConnect.getAccountNumber(),
                                        Optional.ofNullable(humoConnect.getBusinessType()).map(BusinessType::getName).orElse(""),
                                        Optional.ofNullable(humoConnect.getBankType()).map(BankType::getName).orElse(""),
                                        Optional.ofNullable(humoConnect.getPaymentType()).map(PaymentType::getName).orElse(""),
                                        humoConnect.getCustomerName(),
                                        humoConnect.getCustomerTin(),
                                        humoConnect.getDeviceSerialNumber(),
                                        Optional.ofNullable(humoConnect.getCaseType()).map(CaseType::getName).orElse(""),
                                        humoConnect.getCaseId().toString(),
                                        humoConnect.getLicense().toString(),
                                        Optional.ofNullable(humoConnect.getDocFlowStatus()).map(DocFlowStatus::getName).orElse(""),
                                        DateUtil.getLocalDateTimeAsStringWithoutSecond(humoConnect.getFirstReceiptDate()),
                                        DateUtil.getLocalDateTimeAsStringWithoutSecond(humoConnect.getLastReceiptDate()),
                                },
                                false
                        )
                );
    }
}
