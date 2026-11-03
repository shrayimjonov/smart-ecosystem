package uz.uzkassa.smartpossupply.service.handlers.excel.export;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.order.OrderExpeditorReportDTO;
import uz.uzkassa.smartpossupply.filters.OrderFilter;
import uz.uzkassa.smartpossupply.service.OrderService;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OrderExpeditorReportExcelHandler extends AbstractExcelHandler<OrderFilter> {
    OrderService orderService;

    @Override
    public String getFileName(OrderFilter filter) {
        return "OrderExpeditorReport.xlsx";
    }

    @Override
    protected LinkedList<String> getHeaders() {
        LinkedList<String> header = new LinkedList<>();
        header.add("№");
        header.add("Филиал");
        header.add("Склад");
        header.add("Экспедитор");
        header.add("Маршрут");
        header.add("Кол-во заказов");
        header.add("Кол-во отгрузок");
        header.add("Сумма заказов");
        header.add("Кол-во возвратов");
        return header;
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(OrderFilter filter) {
        filter.setSize(1000); //max rows
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        List<OrderExpeditorReportDTO> productReport = orderService.getOrderExpeditorReport(filter).getContent();
        int rowIndex = 0;
        for (OrderExpeditorReportDTO dto : productReport) {
            Row row = sheet.createRow(++rowIndex);
            row.createCell(0).setCellValue(rowIndex);
            row.createCell(1).setCellValue(Optional.ofNullable(dto.getBranch().getName()).orElse(""));
            row.createCell(2).setCellValue(Optional.ofNullable(dto.getWarehouse().getName()).orElse(""));
            row.createCell(3).setCellValue(Optional.ofNullable(dto.getExpeditor().getName()).orElse(""));
            row.createCell(4).setCellValue(Optional.ofNullable(dto.getPlanRoute().getName()).orElse(""));
            row.createCell(5).setCellValue(Optional.ofNullable(dto.getOrderCount()).orElse(0L).toString());
            row.createCell(6).setCellValue(Optional.ofNullable(dto.getStockOutCount()).orElse(0L).toString());
            row.createCell(7).setCellValue(Optional.ofNullable(dto.getTotalOrderPrice()).orElse(BigDecimal.ZERO).toString());
            row.createCell(8).setCellValue(Optional.ofNullable(dto.getReturnedOrderCount()).orElse(0L).toString());
        }
        return workbook;
    }
}
