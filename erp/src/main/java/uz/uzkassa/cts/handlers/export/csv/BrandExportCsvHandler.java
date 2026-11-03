package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.BrandRepository;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class BrandExportCsvHandler extends AbstractExportCsvHandler<BaseFilter> {

    BrandRepository brandRepository;

    @Override
    public String getFileName(BaseFilter filter) {
        return "Brands.csv";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Название");
    }

    @Override
    protected void drawItemList(BaseFilter filter, CSVWriter csvWriter) {
        brandRepository
                .getResultList(filter)
                .getList()
                .forEach(brand ->
                        csvWriter.writeNext(
                                new String[]{
                                        brand.getName()
                                },
                                false
                        )
                );
    }
}
