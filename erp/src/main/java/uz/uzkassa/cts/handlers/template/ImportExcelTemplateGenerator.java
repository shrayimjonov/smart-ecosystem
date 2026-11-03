package uz.uzkassa.cts.handlers.template;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ImportExcelTemplateGenerator {

    String getFileName();

    List<String> getHeaders();

    XSSFWorkbook getWorkbook();

    ResponseEntity<Resource> download();
}
