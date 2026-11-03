package uz.uzkassa.smartpossupply.service.handlers.excel.template;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 05.04.2023
 */
@Component
public class CustomerImportTemplateExcelHandler extends AbstractImportTemplateExcelHandler implements ITemplateExcelHandler {

    @Override
    public List<String> getHeaders() {
        return List.of(
                "ИНН",
                "Название",
                "Бренд",
                "Номер телефона",
                "Адрес",
                "GPS координата"
        );
    }

    @Override
    public String getFileName() {
        return "Customers";
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
