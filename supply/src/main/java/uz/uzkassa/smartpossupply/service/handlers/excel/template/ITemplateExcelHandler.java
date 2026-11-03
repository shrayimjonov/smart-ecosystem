package uz.uzkassa.smartpossupply.service.handlers.excel.template;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ITemplateExcelHandler {

    List<String> getHeaders();

    String getFileName();

    XSSFWorkbook getWorkbook();

    ResponseEntity<Resource> download();
}
