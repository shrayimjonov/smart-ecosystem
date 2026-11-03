package uz.uzkassa.smartpossupply.service.handlers.csv;

import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.smartpossupply.constants.Constants;
import uz.uzkassa.smartpossupply.filters.BaseFilter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.LinkedList;

@Component
@Slf4j
public abstract class AbstractCsvHandler<T extends BaseFilter> {

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
            filter.setSize(Constants.CSV_EXPORT_MAX_LIMIT);
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

    protected abstract String getFileName(T filter);

    protected abstract LinkedList<String> getHeaders();

    protected abstract void drawItemList(T filter, CSVWriter csvWriter);
}
