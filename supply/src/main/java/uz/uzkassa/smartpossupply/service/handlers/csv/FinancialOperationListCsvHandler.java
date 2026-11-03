package uz.uzkassa.smartpossupply.service.handlers.csv;

import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.financialoperation.FinancialOperationListDTO;
import uz.uzkassa.smartpossupply.filters.FinancialOperationFilter;
import uz.uzkassa.smartpossupply.service.FinancialOperationService;

import java.util.LinkedList;
import java.util.List;

@Component
@Slf4j
public class FinancialOperationListCsvHandler extends AbstractCsvHandler<FinancialOperationFilter> {

    private final FinancialOperationService financialOperationService;

    public FinancialOperationListCsvHandler(FinancialOperationService financialOperationService) {
        this.financialOperationService = financialOperationService;
    }

    @Override
    public String getFileName(FinancialOperationFilter filter) {
        return "Orders.csv";
    }

    @Override
    protected LinkedList<String> getHeaders() {
        LinkedList<String> header = new LinkedList<>();
        header.add("Фин. операция");
        header.add("Клиент");
        header.add("Сумма");
        header.add("Отгрузка");
        header.add("№ документа");
        header.add("Способ оплаты");
        header.add("Дата создания");
        header.add("Комментарий");
        return header;
    }

    @Override
    protected void drawItemList(FinancialOperationFilter filter, CSVWriter csvWriter) {
        List<FinancialOperationListDTO> resultList = financialOperationService.getList(filter).getContent();
        resultList
            .forEach(financialOperationListDTO ->
                csvWriter.writeNext(
                    new String[]{
                        financialOperationListDTO.getOperationNumber(),
                        financialOperationListDTO.getCustomer().getName().concat(" ").concat(financialOperationListDTO.getCustomer().getTin()),
                        financialOperationListDTO.getAmount().toString(),
                        financialOperationListDTO.getStockOut() != null ? financialOperationListDTO.getStockOut().getName() : "",
                        financialOperationListDTO.getFactura() != null ? financialOperationListDTO.getFactura().getName() : "",
                        financialOperationListDTO.getPaymentType() != null ? financialOperationListDTO.getPaymentType().getName() : "",
                        financialOperationListDTO.getCreatedDate() != null ? financialOperationListDTO.getCreatedDate().toString() : "",
                        StringUtils.isNotEmpty(financialOperationListDTO.getDescription()) ? financialOperationListDTO.getDescription() : "",
                    },
                    false
                )
            );
    }
}
