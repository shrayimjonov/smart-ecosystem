package uz.uzkassa.cts.handlers.export.excel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.DeviceSuspendHistory;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.filter.DeviceSuspendHistoryFilter;
import uz.uzkassa.cts.repository.DeviceSuspendHistoryRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DeviceSuspendHistoryExportExcelHandler extends AbstractExportExcelHandler<DeviceSuspendHistoryFilter> {

    DeviceSuspendHistoryRepository deviceSuspendHistoryRepository;

    @Override
    public String getFileName(DeviceSuspendHistoryFilter filter) {
        return "Device_Suspend_History.xlsx";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Клиент", "Серийный номер", "Тариф", "Дата начала", "Дата окончания", "Создал", "Последние изменения внес");
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(DeviceSuspendHistoryFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        filter.setPage(0);
        filter.setSize(EXCEL_EXPORT_MAX_LIMIT);
        List<DeviceSuspendHistory> result = deviceSuspendHistoryRepository.getResultList(filter).getList();
        int rowIndex = 0;
        for (DeviceSuspendHistory deviceSuspendHistory : result) {
            Row row = sheet.createRow(++rowIndex);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(deviceSuspendHistory.getContract().getCustomer().getName());
            row.createCell(cellIndex++).setCellValue(deviceSuspendHistory.getDeviceSerialNumber());
            row.createCell(cellIndex++).setCellValue(deviceSuspendHistory.getTariff().getName());
            row.createCell(cellIndex++).setCellValue(DateUtil.getLocalDateAsString(deviceSuspendHistory.getStartDate()));
            row.createCell(cellIndex++).setCellValue(DateUtil.getLocalDateAsString(deviceSuspendHistory.getDueDate()));
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(deviceSuspendHistory.getCreator()).map(User::getName).orElse(""));
            row.createCell(cellIndex).setCellValue(Optional.ofNullable(deviceSuspendHistory.getLastModifier()).map(User::getName).orElse(""));
        }
        return workbook;
    }
}
