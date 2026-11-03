package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.BankRepository;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class BankExportCsvHandler extends AbstractExportCsvHandler<BaseFilter> {

    BankRepository bankRepository;

    @Override
    public String getFileName(BaseFilter filter) {
        return "Banks.csv";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Название", "МФО", "Статус");
    }

    @Override
    protected void drawItemList(BaseFilter filter, CSVWriter csvWriter) {
        bankRepository
                .getResultList(filter)
                .getList()
                .forEach(bank ->
                        csvWriter.writeNext(
                                new String[]{
                                        bank.getNameRu(),
                                        bank.getCode(),
                                        bank.getStatus().getName()
                                },
                                false
                        )
                );
    }
}
