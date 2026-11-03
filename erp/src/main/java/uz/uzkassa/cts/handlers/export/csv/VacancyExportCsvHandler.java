package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.filter.VacancyFilter;
import uz.uzkassa.cts.repository.VacancyRepository;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class VacancyExportCsvHandler extends AbstractExportCsvHandler<VacancyFilter> {

    VacancyRepository vacancyRepository;

    @Override
    public String getFileName(VacancyFilter filter) {
        return "Vacancies.csv";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("ФИО заявителя", "Название", "Отдел", "Должность", "Статус", "Заявка");
    }

    @Override
    protected void drawItemList(VacancyFilter filter, CSVWriter csvWriter) {
        vacancyRepository
                .getResultList(filter)
                .getList()
                .forEach(vacancy ->
                        csvWriter.writeNext(
                                new String[]{
                                        vacancy.getCreator().getName(),
                                        vacancy.getName(),
                                        vacancy.getDepartment().getName(),
                                        vacancy.getPosition().getName(),
                                        vacancy.getStatus().getName(),
                                        vacancy.getApplication().getName()
                                },
                                false
                        )
                );
    }
}
