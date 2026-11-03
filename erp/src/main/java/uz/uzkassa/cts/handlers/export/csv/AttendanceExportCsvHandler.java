package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.filter.AttendanceFilter;
import uz.uzkassa.cts.repository.AttendanceRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AttendanceExportCsvHandler extends AbstractExportCsvHandler<AttendanceFilter> {

    AttendanceRepository attendanceRepository;

    @Override
    public String getFileName(AttendanceFilter filter) {
        return "Attendances_" + filter.getMonth() + "_" + filter.getYear() + ".csv";
    }

    @Override
    protected List<String> getHeaders() {
        return null;
    }

    @Override
    protected void drawItemList(AttendanceFilter filter, CSVWriter csvWriter) {
        attendanceRepository
                .getResultList(filter)
                .getList()
                .forEach(attendance -> {
                            List<String> cells = new ArrayList<>();
                            cells.add(attendance.getEmployee().getFullName());
                            attendance.getItems().forEach(attendanceItem -> cells.add(attendanceItem.getHolidayId() != null ? attendanceItem.getHoliday().getName() : attendanceItem.getLeaveRequestId() != null ? attendanceItem.getLeaveRequest().getName() : DateUtil.convertMinutesToHoursAsString(attendanceItem.getActualHours())));
                            cells.add(attendance.getNormalDays().toString());
                            cells.add(attendance.getActualDays().toString());
                            cells.add(DateUtil.convertMinutesToHoursAsString(attendance.getMonthlyPlannedHours()));
                            cells.add(DateUtil.convertMinutesToHoursAsString(attendance.getMonthlyActualHours()));
                            csvWriter.writeNext(cells.toArray(new String[0]), false);
                        }
                );
    }
}
