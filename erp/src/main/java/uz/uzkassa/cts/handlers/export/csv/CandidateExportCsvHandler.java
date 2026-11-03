package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.CandidateRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CandidateExportCsvHandler extends AbstractExportCsvHandler<BaseFilter> {

    CandidateRepository candidateRepository;

    @Override
    public String getFileName(BaseFilter filter) {
        return "Candidates.csv";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("ФИО", "Компания", "Филиал", "Телефон", "Позиция", "Вакансия", "Дата создания");
    }

    @Override
    protected void drawItemList(BaseFilter filter, CSVWriter csvWriter) {
        candidateRepository
                .getResultList(filter)
                .getList()
                .forEach(candidate ->
                        csvWriter.writeNext(
                                new String[]{
                                        candidate.getFullName(),
                                        candidate.getDepartment().getBranch().getCompany().getName(),
                                        candidate.getDepartment().getBranch().getName(),
                                        candidate.getPhone(),
                                        candidate.getPosition().getName(),
                                        candidate.getVacancy().getName(),
                                        DateUtil.getLocalDateTimeAsStringWithoutSecond(candidate.getCreatedDate())
                                },
                                false
                        )
                );
    }
}
