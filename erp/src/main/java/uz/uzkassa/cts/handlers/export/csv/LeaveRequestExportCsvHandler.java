package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.LeaveRequestRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.LinkedList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class LeaveRequestExportCsvHandler extends AbstractExportCsvHandler<BaseFilter> {

    LeaveRequestRepository leaveRequestRepository;

    @Override
    public String getFileName(BaseFilter filter) {
        return "LeaveRequests.csv";
    }

    @Override
    protected List<String> getHeaders() {
        List<String> header = new LinkedList<>();
        header.add("Номер");
        header.add("Компания");
        header.add("Дата начало");
        header.add("Дата окончания");
        header.add("Продолжительность");
        header.add("Причина");
        header.add("Статус");
        return header;
    }

    @Override
    protected void drawItemList(BaseFilter filter, CSVWriter csvWriter) {
        leaveRequestRepository
                .getResultList(filter)
                .getList()
                .forEach(leaveRequest ->
                        csvWriter.writeNext(
                                new String[]{
                                        leaveRequest.getLeaveRequestNumber(),
                                        leaveRequest.getCompany().getName(),
                                        DateUtil.getLocalDateTimeAsString(leaveRequest.getFromDate()),
                                        DateUtil.getLocalDateTimeAsString(leaveRequest.getToDate()),
                                        DateUtil.convertMinutesToString(leaveRequest.getDuration()),
                                        leaveRequest.getReason().getName(),
                                        leaveRequest.getStatus().getName()
                                },
                                false
                        )
                );
    }
}
