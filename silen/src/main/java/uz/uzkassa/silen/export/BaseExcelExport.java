package uz.uzkassa.silen.export;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import uz.uzkassa.silen.domain.ExportReport;
import uz.uzkassa.silen.exceptions.BadRequestException;

import java.io.IOException;
import java.io.OutputStream;

@Slf4j
@Getter
public abstract class BaseExcelExport {
    protected int pageSizeLimit = 10_000;
    protected int pageNumber = 0;
    protected String sheetName = "Отчет";
    @Autowired
    protected ObjectMapper objectMapper;

    protected abstract void writeHeader(ExportReport exportReport, XSSFSheet sheet);

    protected abstract void writeBody(ExportReport exportReport, XSSFSheet sheet);

    public void writeToStream(ExportReport exportReport, OutputStream outputStream) {

        try (XSSFWorkbook wb = new XSSFWorkbook()) {

            XSSFSheet sheet = wb.createSheet(getSheetName());

            // write header context
            writeHeader(exportReport, sheet);

            // write content
            writeBody(exportReport, sheet);

            wb.write(outputStream);
        } catch (IOException e) {
            log.error("Error on time write to stream", e);
        }
    }

    protected <T> T readFilter(String filter, Class<T> clazz) {
        try {
            return objectMapper.readValue(filter, clazz);
        } catch (JsonProcessingException ignored) {
            throw new BadRequestException("An unaccepted error has occurred while reading filters");
        }
    }
}
