package uz.uzkassa.smartpossupply.service.handlers.excel.template;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 05.04.2023
 */
@Component
public class OrderImportTemplateExcelHandler extends AbstractImportTemplateExcelHandler implements ITemplateExcelHandler {

    @Override
    public List<String> getHeaders() {
        return Arrays.asList(
            "№Счет-фактуры",
            "Дата Счет-фактуры",
            "Договор №",
            "Дата Договора",
            "ИНН покупателя",
            "Наименование покупателя",
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
        return "Orders";
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
