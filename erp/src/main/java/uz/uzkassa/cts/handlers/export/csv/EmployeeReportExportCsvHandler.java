package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.filter.EmployeeReportFilter;
import uz.uzkassa.cts.service.ReportService;

import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class EmployeeReportExportCsvHandler extends AbstractExportCsvHandler<EmployeeReportFilter> {

    ReportService reportService;

    @Override
    public String getFileName(EmployeeReportFilter filter) {
        return "Employee_Report.csv";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Имя", "Филиал", "Клиенты", "Торговые точки", "ККМ", "ФМ", "Тариф", "Тариф (Бонус)", "Сервис", "Сервис (Бонус)", "Лизинг", "Лизинг (Бонус)", "Trade In", "Trade In (Бонус)", "Аренда", "Аренда (Бонус)", "Бонус");
    }

    @Override
    protected void drawItemList(EmployeeReportFilter filter, CSVWriter csvWriter) {
        reportService
                .getStatisticsByEmployeeReport(filter)
                .forEach(reportDTO ->
                        csvWriter.writeNext(
                                new String[]{
                                        Optional.ofNullable(reportDTO.getUser()).map(CommonDTO::getName).orElse(""),
                                        Optional.ofNullable(reportDTO.getBranch()).map(CommonDTO::getName).orElse(""),
                                        reportDTO.getCustomerTotal().toString(),
                                        reportDTO.getCustomerBranchTotal().toString(),
                                        reportDTO.getDeviceTotal().toString(),
                                        reportDTO.getFiscalModuleTotal().toString(),
                                        reportDTO.getTariffTotal().toString(),
                                        reportDTO.getTariffTotalBonus().toString(),
                                        reportDTO.getServiceTotal().toString(),
                                        reportDTO.getServiceTotalBonus().toString(),
                                        reportDTO.getLeasingTotal().toString(),
                                        reportDTO.getLeasingTotalBonus().toString(),
                                        reportDTO.getTradeInTotal().toString(),
                                        reportDTO.getTradeInTotalBonus().toString(),
                                        reportDTO.getRentTotal().toString(),
                                        reportDTO.getRentTotalBonus().toString(),
                                        String.valueOf((reportDTO.getTariffTotalBonus() + reportDTO.getServiceTotalBonus() + reportDTO.getLeasingTotalBonus() + reportDTO.getTradeInTotalBonus() + reportDTO.getRentTotalBonus()))
                                },
                                false
                        )
                );
    }
}
