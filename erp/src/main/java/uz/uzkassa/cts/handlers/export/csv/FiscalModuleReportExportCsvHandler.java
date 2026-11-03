package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.address.BaseAddress;
import uz.uzkassa.cts.domain.address.Region;
import uz.uzkassa.cts.enums.ArrivalStatus;
import uz.uzkassa.cts.enums.FMReportReason;
import uz.uzkassa.cts.filter.FiscalModuleReportFilter;
import uz.uzkassa.cts.repository.FiscalModuleReportRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class FiscalModuleReportExportCsvHandler extends AbstractExportCsvHandler<FiscalModuleReportFilter> {

    FiscalModuleReportRepository fiscalModuleReportRepository;

    @Override
    public String getFileName(FiscalModuleReportFilter filter) {
        return "Fiscal_Module_Report.csv";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("ИНН", "Клиент", "ККМ", "ФМ", "Статус принятие кейса", "Статус прихода ФМ", "Дата прихода ФМ", "Оператор", "Тип кейса", "Регион", "Кейс ID", "Дата принятие кейса");
    }

    @Override
    protected void drawItemList(FiscalModuleReportFilter filter, CSVWriter csvWriter) {
        fiscalModuleReportRepository
                .getResultList(filter)
                .getList()
                .forEach(fiscalModuleReport ->
                        csvWriter.writeNext(
                                new String[]{
                                        fiscalModuleReport.getCustomer().getTin(),
                                        fiscalModuleReport.getCustomer().getName(),
                                        fiscalModuleReport.getDeviceSerialNumber(),
                                        fiscalModuleReport.getFmSerialNumber(),
                                        Optional.ofNullable(fiscalModuleReport.getReason()).map(FMReportReason::getName).orElse(""),
                                        Optional.ofNullable(fiscalModuleReport.getArrivalStatus()).map(ArrivalStatus::getName).orElse(""),
                                        DateUtil.getLocalDateTimeAsStringWithoutSecond(fiscalModuleReport.getArrivalDate()),
                                        Optional.ofNullable(fiscalModuleReport.getCases()).map(cases -> cases.getCreator().getName()).orElse(""),
                                        Optional.ofNullable(fiscalModuleReport.getCases()).map(cases -> cases.getCaseType().getName()).orElse(""),
                                        Optional.ofNullable(fiscalModuleReport.getCustomer().getAddress()).map(BaseAddress::getRegion).map(Region::getName).orElse(""),
                                        Optional.ofNullable(fiscalModuleReport.getCaseId()).map(Object::toString).orElse(""),
                                        DateUtil.getLocalDateTimeAsStringWithoutSecond(fiscalModuleReport.getCreatedDate())
                                },
                                false
                        )
                );
    }
}
