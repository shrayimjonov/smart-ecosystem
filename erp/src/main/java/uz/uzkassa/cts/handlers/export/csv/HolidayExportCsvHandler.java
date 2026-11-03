package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.HolidayRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.LinkedList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class HolidayExportCsvHandler extends AbstractExportCsvHandler<BaseFilter> {

    HolidayRepository holidayRepository;

    @Override
    public String getFileName(BaseFilter filter) {
        return "Holidays.csv";
    }

    @Override
    protected List<String> getHeaders() {
        List<String> header = new LinkedList<>();
        header.add("Название");
        header.add("Компания");
        header.add("Дата начало");
        header.add("Дата окончания");
        header.add("Выходной");
        header.add("Статус");
        return header;
    }

    @Override
    protected void drawItemList(BaseFilter filter, CSVWriter csvWriter) {
        holidayRepository
                .getResultList(filter)
                .getList()
                .forEach(holiday ->
                        csvWriter.writeNext(
                                new String[]{
                                        holiday.getName(),
                                        holiday.getCompany().getName(),
                                        DateUtil.getLocalDateAsString(holiday.getFromDate()),
                                        DateUtil.getLocalDateAsString(holiday.getToDate()),
                                        String.valueOf(holiday.isDayOff()),
                                        holiday.getStatus().getName()
                                },
                                false
                        )
                );
    }
}
