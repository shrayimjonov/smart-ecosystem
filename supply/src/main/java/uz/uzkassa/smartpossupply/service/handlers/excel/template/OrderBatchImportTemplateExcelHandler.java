package uz.uzkassa.smartpossupply.service.handlers.excel.template;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;


@Component
public class OrderBatchImportTemplateExcelHandler extends AbstractImportTemplateExcelHandler implements ITemplateExcelHandler {

    @Override
    public List<String> getHeaders() {
        return Arrays.asList(
                "№Счет-фактуры",
                "Дата Счет-фактуры",
                "Договор №",
                "Дата Договора",
                "ИНН покупателя",
                "Код покупателя",
                "Код тарговой точки",
                "Наименование  товара",
                "ИКПУ",
                "Штрих код",
                "Код Ед. измерения",
                "Ед. измерения",
                "Количество",
                "Серия Товара",
                "Базовая цена",
                "Наценка",
                "Цена",
                "Сумма поставки",
                "НДС %",
                "НДС % Сумма",
                "Стоимость поставки с учетом НДС %"
        );
    }

    @Override
    public String getFileName() {
        return "OrderBatch";
    }

    @Override
    public XSSFWorkbook getWorkbook() {
        return getXSSFWorkbook(getFileName(), getHeaders());
    }

    @Override
    public ResponseEntity<Resource> download() {
        return getTemplate(getWorkbook(), getFileName());
    }
}
