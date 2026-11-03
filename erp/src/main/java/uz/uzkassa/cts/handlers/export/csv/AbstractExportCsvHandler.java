package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.cts.filter.BaseFilter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.List;

@Component
@Slf4j
public abstract class AbstractExportCsvHandler<T extends BaseFilter> {

    protected static final DecimalFormat numberFormat = new DecimalFormat("#,###.0000");

    public byte[] generate(T filter) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        OutputStreamWriter streamWriter = new OutputStreamWriter(stream, StandardCharsets.UTF_8);
        try {
            CSVWriter csvWriter = new CSVWriter(
                streamWriter,
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.NO_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END
            );
            if (!CollectionUtils.isEmpty(getHeaders())) {
                csvWriter.writeNext(getHeaders().toArray(new String[]{}), false);
            }
            filter.setPage(0);
            filter.setSize(1000000);
            drawItemList(filter, csvWriter);
            csvWriter.close();
            byte[] bytes = stream.toByteArray();
            streamWriter.close();
            return bytes;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public abstract String getFileName(T filter);

    protected abstract List<String> getHeaders();

    protected abstract void drawItemList(T filter, CSVWriter csvWriter);
}
