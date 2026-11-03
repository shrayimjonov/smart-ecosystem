package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.BonusRepository;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class BonusExportCsvHandler extends AbstractExportCsvHandler<BaseFilter> {

    BonusRepository bonusRepository;

    @Override
    public String getFileName(BaseFilter filter) {
        return "Bonuses.csv";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Сотрудник", "Компания", "Филиал", "Количество", "Сумма", "Сумма бонуса");
    }

    @Override
    protected void drawItemList(BaseFilter filter, CSVWriter csvWriter) {
        bonusRepository
                .getResultList(filter)
                .getList()
                .forEach(bonusListDTO ->
                        csvWriter.writeNext(
                                new String[]{
                                        bonusListDTO.getEmployee(),
                                        bonusListDTO.getCompany(),
                                        bonusListDTO.getBranch(),
                                        bonusListDTO.getCount().toString(),
                                        bonusListDTO.getAmount().toString(),
                                        bonusListDTO.getBonusAmount().toString()
                                },
                                false
                        )
                );
    }
}
