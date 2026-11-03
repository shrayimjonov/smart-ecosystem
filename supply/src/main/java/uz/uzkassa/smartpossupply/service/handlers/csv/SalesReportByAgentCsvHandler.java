package uz.uzkassa.smartpossupply.service.handlers.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.filters.PlanRouteFilter;
import uz.uzkassa.smartpossupply.service.PlanRouteService;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SalesReportByAgentCsvHandler extends AbstractCsvHandler<PlanRouteFilter> {

    PlanRouteService planRouteService;

    @Override
    public String getFileName(PlanRouteFilter filter) {
        return "SalesReport.csv";
    }

    @Override
    protected LinkedList<String> getHeaders() {
        LinkedList<String> header = new LinkedList<>();
        header.add("Торговый агент");
        header.add("Клиент название + ИНН");
        header.add("Т.Т. Клиента");
        header.add("Стоимость без НДС");
        header.add("НДС сумма");
        header.add("Стоимость с НДС");
        return header;
    }

    @Override
    protected void drawItemList(PlanRouteFilter filter, CSVWriter csvWriter) {
        planRouteService
                .reportList(filter)
                .forEach(productReportListDTO ->
                        csvWriter.writeNext(
                                new String[]{
                                        productReportListDTO.getAgent().getName(),
                                        productReportListDTO.getCustomer().getName().concat(" ".concat(productReportListDTO.getCustomer().getTin())),
                                        Optional.ofNullable(productReportListDTO.getBranch() != null ? productReportListDTO.getBranch().getName() : null).orElse(""),
                                        Optional.ofNullable(productReportListDTO.getTotalPriceWithoutVat()).orElse(BigDecimal.ZERO).toString(),
                                        Optional.ofNullable(productReportListDTO.getTotalVatPrice()).orElse(BigDecimal.ZERO).toString(),
                                        Optional.ofNullable(productReportListDTO.getTotalPrice()).orElse(BigDecimal.ZERO).toString(),
                                },
                                false
                        )
                );
    }
}
