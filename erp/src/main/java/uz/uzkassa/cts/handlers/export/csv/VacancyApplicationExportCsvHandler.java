package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.VacancyApplicationRepository;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class VacancyApplicationExportCsvHandler extends AbstractExportCsvHandler<BaseFilter> {

    VacancyApplicationRepository vacancyApplicationRepository;

    @Override
    public String getFileName(BaseFilter filter) {
        return "Vacancy_Applications.csv";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("ФИО", "Компания", "Филиал", "Отдел", "Позиция", "Статус");
    }

    @Override
    protected void drawItemList(BaseFilter filter, CSVWriter csvWriter) {
        vacancyApplicationRepository
                .getResultList(filter)
                .getList()
                .forEach(vacancyApplication ->
                        csvWriter.writeNext(
                                new String[]{
                                        vacancyApplication.getName(),
                                        vacancyApplication.getDepartment().getBranch().getCompany().getName(),
                                        vacancyApplication.getDepartment().getBranch().getName(),
                                        vacancyApplication.getDepartment().getName(),
                                        vacancyApplication.getPosition().getName(),
                                        vacancyApplication.getStatus().getName()
                                },
                                false
                        )
                );
    }
}
