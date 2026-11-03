package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.filter.FeedbackFilter;
import uz.uzkassa.cts.repository.FeedbackRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class FeedbackExportCsvHandler extends AbstractExportCsvHandler<FeedbackFilter> {

    FeedbackRepository feedbackRepository;

    @Override
    public String getFileName(FeedbackFilter filter) {
        return "Feedback.csv";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Сотрудник", "Регион", "Клиент", "ТТ", "Тип", "Оценка", "Статус", "Дата");
    }

    @Override
    protected void drawItemList(FeedbackFilter filter, CSVWriter csvWriter) {
        feedbackRepository
                .getResultList(filter)
                .getList()
                .forEach(feedback ->
                        csvWriter.writeNext(
                                new String[]{
                                        feedback.getCreator().getFullName(),
                                        feedback.getCustomerBranch().getAddress().getRegion().getName(),
                                        feedback.getCustomerBranch().getCustomer().getName(),
                                        feedback.getCustomerBranch().getName(),
                                        feedback.getType().getName(),
                                        feedback.getRate().toString(),
                                        feedback.getStatus().getName(),
                                        DateUtil.getLocalDateTimeAsStringWithoutSecond(feedback.getCreatedDate())
                                },
                                false
                        )
                );
    }
}
