package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.PositionRepository;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PositionExportCsvHandler extends AbstractExportCsvHandler<BaseFilter> {

    PositionRepository positionRepository;

    @Override
    public String getFileName(BaseFilter filter) {
        return "Positions.csv";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Название", "Статус");
    }

    @Override
    protected void drawItemList(BaseFilter filter, CSVWriter csvWriter) {
        positionRepository
                .findAllByDeletedIsFalse()
                .forEach(position ->
                        csvWriter.writeNext(
                                new String[]{
                                        position.getName(),
                                        position.getStatus().getName()
                                },
                                false
                        )
                );
    }
}
