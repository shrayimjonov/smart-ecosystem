package uz.uzkassa.cts.handlers.export.excel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.hrms.LeaveRequest;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.LeaveRequestRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.LinkedList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class LeaveRequestExportExcelHandler extends AbstractExportExcelHandler<BaseFilter> {

    LeaveRequestRepository leaveRequestRepository;

    @Override
    public String getFileName(BaseFilter filter) {
        return "LeaveRequests.xlsx";
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
    protected XSSFWorkbook getXSSFWorkbook(BaseFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        filter.setPage(0);
        filter.setSize(EXCEL_EXPORT_MAX_LIMIT);
        List<LeaveRequest> result = leaveRequestRepository.getResultList(filter).getList();
        int rowIndex = 0;
        for (LeaveRequest leaveRequest : result) {
            Row row = sheet.createRow(++rowIndex);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(leaveRequest.getLeaveRequestNumber());
            row.createCell(cellIndex++).setCellValue(leaveRequest.getCompany().getName());
            row.createCell(cellIndex++).setCellValue(DateUtil.getLocalDateTimeAsString(leaveRequest.getFromDate()));
            row.createCell(cellIndex++).setCellValue(DateUtil.getLocalDateTimeAsString(leaveRequest.getToDate()));
            row.createCell(cellIndex++).setCellValue(DateUtil.convertMinutesToString(leaveRequest.getDuration()));
            row.createCell(cellIndex++).setCellValue(leaveRequest.getReason().getName());
            row.createCell(cellIndex).setCellValue(leaveRequest.getStatus().getName());
        }
        return workbook;
    }
}
