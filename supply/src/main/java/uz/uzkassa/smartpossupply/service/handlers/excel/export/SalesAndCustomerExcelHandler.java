package uz.uzkassa.smartpossupply.service.handlers.excel.export;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.order.SalesAndCustomerReportDTO;
import uz.uzkassa.smartpossupply.filters.OrderFilter;
import uz.uzkassa.smartpossupply.service.OrderService;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SalesAndCustomerExcelHandler extends AbstractExcelHandler<OrderFilter> {

    OrderService orderService;

    @Override
    public String getFileName(OrderFilter filter) {
        return "SalesCustomerReport.xlsx";
    }

    @Override
    protected LinkedList<String> getHeaders() {
        LinkedList<String> header = new LinkedList<>();
        header.add("Категория");
        header.add("Клиент");
        header.add("ИНН");
        header.add("ТТ клиента");
        header.add("Товар");
        header.add("Штрихкод");
        header.add("Контракт");
        header.add("Цена контракта");
        header.add("Количество");
        header.add("Серия");
        header.add("Базовая цена");
        header.add("Наценка %");
        header.add("Наценка сумма");
        header.add("Стоимость без НДС");
        header.add("НДС сумма");
        header.add("Стоимость с НДС");
        header.add("Филиат откуда");
        header.add("Склад откуда");
        return header;
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(OrderFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);

        int page = 0;
        int size = 1000;
        int rowIndex = 0;

        Page<SalesAndCustomerReportDTO> pageResult;

        do {
            filter.setPage(page);
            filter.setSize(size);
            pageResult = orderService.getSalesAndCustomerReport(filter);

            for (SalesAndCustomerReportDTO salesAndCustomerReportDTO : pageResult) {
                Row row = sheet.createRow(++rowIndex);
                row.createCell(0).setCellValue(Optional.ofNullable(salesAndCustomerReportDTO.getCategoryName()).orElse(""));
                row.createCell(1).setCellValue(Optional.ofNullable(salesAndCustomerReportDTO.getCustomerName()).orElse(""));
                row.createCell(2).setCellValue(Optional.ofNullable(salesAndCustomerReportDTO.getCustomerTin()).orElse(""));
                row.createCell(3).setCellValue(Optional.ofNullable(salesAndCustomerReportDTO.getCustomerBranch()).orElse(""));
                row.createCell(4).setCellValue(Optional.ofNullable(salesAndCustomerReportDTO.getProductName()).orElse(""));
                row.createCell(5).setCellValue(Optional.ofNullable(salesAndCustomerReportDTO.getBarcode()).orElse(""));
                row.createCell(6).setCellValue(Optional.ofNullable(salesAndCustomerReportDTO.getContractNumber()).orElse(""));
                row.createCell(7).setCellValue(Optional.ofNullable(salesAndCustomerReportDTO.getContractPrice()).orElse(BigDecimal.ZERO).toString());
                row.createCell(8).setCellValue(Optional.ofNullable(salesAndCustomerReportDTO.getQuantity()).orElse(BigDecimal.ZERO).toString());
                row.createCell(9).setCellValue(Optional.ofNullable(salesAndCustomerReportDTO.getSerialNumber()).orElse(""));
                row.createCell(10).setCellValue(Optional.ofNullable(salesAndCustomerReportDTO.getBasePrice()).orElse(BigDecimal.ZERO).toString());
                row.createCell(11).setCellValue(Optional.ofNullable(salesAndCustomerReportDTO.getMarkupPercent()).orElse(BigDecimal.ZERO).toString());
                row.createCell(12).setCellValue(Optional.ofNullable(salesAndCustomerReportDTO.getMarkupAmount()).orElse(BigDecimal.ZERO).toString());
                row.createCell(13).setCellValue(Optional.ofNullable(salesAndCustomerReportDTO.getPriceWithoutVat()).orElse(BigDecimal.ZERO).toString());
                row.createCell(14).setCellValue(Optional.ofNullable(salesAndCustomerReportDTO.getVatPrice()).orElse(BigDecimal.ZERO).toString());
                row.createCell(15).setCellValue(Optional.ofNullable(salesAndCustomerReportDTO.getPriceWithVat()).orElse(BigDecimal.ZERO).toString());
                row.createCell(16).setCellValue(Optional.ofNullable(salesAndCustomerReportDTO.getBranch()).orElse(""));
                row.createCell(17).setCellValue(Optional.ofNullable(salesAndCustomerReportDTO.getWarehouse()).orElse(""));
            }
            page++;
        } while (!pageResult.isLast());

        return workbook;
    }

}
