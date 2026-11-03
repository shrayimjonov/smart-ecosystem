package uz.uzkassa.silen.export;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import uz.uzkassa.silen.domain.ExportReport;
import uz.uzkassa.silen.exceptions.BadRequestException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

@Slf4j
@Getter
public abstract class BaseCsvExport {
    protected int pageNumber = 0;
    protected int pageSizeLimit = 10_000;
    protected boolean withBoom = false;
    @Autowired
    protected ObjectMapper objectMapper;

    protected abstract void writeContent(ExportReport exportReport, CSVWriter csvWriter);

    protected abstract void writeHeader(ExportReport exportReport, CSVWriter csvWriter);

    public void writeToStream(ExportReport exportReport, OutputStream outputStream) {
        try (OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
             CSVWriter csvWriter = new CSVWriter(streamWriter, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.NO_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END)) {

            // this is changeable by child. By default, false
            if (isWithBoom()) {
                addBOM(outputStream);
            }

            // writing header context
            writeHeader(exportReport, csvWriter);

            // write all content by writer
            writeContent(exportReport, csvWriter);

        } catch (IOException e) {
            log.error("Error on time write to stream", e);
        }
    }

    private void addBOM(OutputStream outputStream) {
        byte[] bom = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        try {
            if (outputStream != null) {
                outputStream.write(bom);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
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
