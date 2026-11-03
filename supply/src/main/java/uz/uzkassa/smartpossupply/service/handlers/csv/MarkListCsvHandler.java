package uz.uzkassa.smartpossupply.service.handlers.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.filters.MarkFilter;
import uz.uzkassa.smartpossupply.service.MarkService;

import java.time.LocalDate;
import java.util.LinkedList;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MarkListCsvHandler extends AbstractCsvHandler<MarkFilter> {

    MarkService markService;

    @Override
    public String getFileName(MarkFilter filter) {
        StringBuilder fileName = new StringBuilder();
        fileName.append("Marks_From_");
        fileName.append(LocalDate.now());
        fileName.append(".csv");
        return fileName.toString();
    }

    @Override
    protected LinkedList<String> getHeaders() {
        return new LinkedList<>();
    }

    @Override
    protected void drawItemList(MarkFilter filter, CSVWriter csvWriter) {
        filter.setPage(0);
        filter.setSize(150000);
        markService.getList(filter).toList().forEach(mark -> csvWriter.writeNext(new String[]{mark.getPrintCode()}, false));
    }
}
