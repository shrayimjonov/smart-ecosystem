package uz.uzkassa.cts.handlers.export.excel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.RepairTicket;
import uz.uzkassa.cts.enums.RepairTicketVisitType;
import uz.uzkassa.cts.enums.RepairType;
import uz.uzkassa.cts.filter.RepairTicketFilter;
import uz.uzkassa.cts.repository.RepairTicketRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RepairTicketExportExelHandler extends AbstractExportExcelHandler<RepairTicketFilter> {

    RepairTicketRepository repairTicketRepository;

    @Override
    public String getFileName(RepairTicketFilter filter) {
        return "Repair_Tickets.xlsx";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Клиент", "Клиент ИНН", "ККМ", "Номер  телефона", "Филиал", "Дата создания тикета", "Тип тикета", "Статус", "Дата приема", "Визит", "Тип ремонта");
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(RepairTicketFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        filter.setPage(0);
        filter.setSize(EXCEL_EXPORT_MAX_LIMIT);
        List<RepairTicket> result = repairTicketRepository.getResultList(filter).getList();
        int rowIndex = 0;
        for (RepairTicket repairTicket : result) {
            Row row = sheet.createRow(++rowIndex);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(repairTicket.getCustomer().getName());
            row.createCell(cellIndex++).setCellValue(repairTicket.getCustomer().getTin());
            row.createCell(cellIndex++).setCellValue(repairTicket.getDevice().getSerialNumber());
            row.createCell(cellIndex++).setCellValue(repairTicket.getPhone());
            row.createCell(cellIndex++).setCellValue(repairTicket.getFromBranch().getAddress().getRegion().getName());
            row.createCell(cellIndex++).setCellValue(DateUtil.getLocalDateTimeAsStringWithoutSecond(repairTicket.getCreatedDate()));
            row.createCell(cellIndex++).setCellValue(repairTicket.getType().getName());
            row.createCell(cellIndex++).setCellValue(repairTicket.getStatus().getName());
            row.createCell(cellIndex++).setCellValue(DateUtil.getLocalDateTimeAsStringWithoutSecond(repairTicket.getAcceptedDate()));
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(repairTicket.getVisitType()).map(RepairTicketVisitType::getName).orElse(""));
            row.createCell(cellIndex).setCellValue(Optional.ofNullable(repairTicket.getRepairType()).map(RepairType::getName).orElse(""));
        }
        return workbook;
    }
}
