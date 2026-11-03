package uz.uzkassa.smartpossupply.service.handlers.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.customer.AccountReceivableReportDTO;
import uz.uzkassa.smartpossupply.filters.StockFilter;
import uz.uzkassa.smartpossupply.service.FinancialOperationService;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AccountReceivableCsvHandler extends AbstractCsvHandler<StockFilter> {

    FinancialOperationService financialOperationService;

    @Override
    public String getFileName(StockFilter filter) {
        return "AccountReceivableReport.csv";
    }

    @Override
    protected LinkedList<String> getHeaders() {
        LinkedList<String> header = new LinkedList<>();
        header.add("Клиент");
        header.add("ИНН");
        header.add("Маршрут");
        header.add("Агент");
        header.add("Экспедитор");
        header.add("Номер отгрузки");
        header.add("Дата отгрузки");
        header.add("№ ЭСФ");
        header.add("Дата ЭСФ");
        header.add("Сумма задолженности");
        header.add("Сумма предоплаты");
        header.add("Итого");
        return header;
    }

    @Override
    protected void drawItemList(StockFilter filter, CSVWriter csvWriter) {
        List<AccountReceivableReportDTO> resultList = financialOperationService.getAccountReceivableReportToList(filter);
        resultList
                .forEach(accountReceivableReportDTO ->
                        csvWriter.writeNext(
                                new String[]{
                                        accountReceivableReportDTO.getCustomer() != null ? accountReceivableReportDTO.getCustomer().getName() : "",
                                        accountReceivableReportDTO.getCustomer() != null ? accountReceivableReportDTO.getCustomer().getTin() : "",
                                        accountReceivableReportDTO.getPlanRoute() != null ? accountReceivableReportDTO.getPlanRoute().getName() : "",
                                        accountReceivableReportDTO.getAgent() != null ? accountReceivableReportDTO.getAgent().getName() : "",
                                        accountReceivableReportDTO.getExpeditor() != null ? accountReceivableReportDTO.getExpeditor().getName() : "",
                                        accountReceivableReportDTO.getStockOut() != null ? accountReceivableReportDTO.getStockOut().getName() : "",
                                        accountReceivableReportDTO.getOperationDate() != null ? accountReceivableReportDTO.getOperationDate().toString() : "",
                                        accountReceivableReportDTO.getFactura() != null ? accountReceivableReportDTO.getFactura().getFacturaNo() : "",
                                        accountReceivableReportDTO.getFactura() != null ? accountReceivableReportDTO.getFactura().getFacturaNo() : "",
                                        Optional.ofNullable(accountReceivableReportDTO.getTotalCredit()).orElse(BigDecimal.ZERO).toString(),
                                        Optional.ofNullable(accountReceivableReportDTO.getTotalDebit()).orElse(BigDecimal.ZERO).toString(),
                                        Optional.ofNullable(accountReceivableReportDTO.getTotalDebit()).orElse(BigDecimal.ZERO).subtract(Optional.ofNullable(accountReceivableReportDTO.getTotalCredit()).orElse(BigDecimal.ZERO)).toString(),
                                },
                                false
                        )
                );
    }
}
