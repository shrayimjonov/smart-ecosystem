package uz.uzkassa.smartpossupply.service.handlers.excel.export;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.stocktransfer.ExpeditorStockTransferDetailDTO;
import uz.uzkassa.smartpossupply.dto.stocktransfer.ExpeditorStockTransferProductDetailDTO;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.service.StockTransferService;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ExpeditorStockTransferDetailExcelHandler extends AbstractExcelHandler<BaseFilter> {

    private final StockTransferService stockTransferService;

    @Override
    public String getFileName(BaseFilter filter) {
        return "expeditor_stock_transfer.xlsx";
    }

    @Override
    protected LinkedList<String> getHeaders() {
        LinkedList<String> header = new LinkedList<>();
        header.add("Товар");
        header.add("Штрихкод");
        header.add("Передано экспедитором");
        header.add("Продано");
        header.add("Остаток");
        header.add("Количество в блоке");
        return header;
    }
    @Override
    protected XSSFWorkbook getXSSFWorkbook(BaseFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        ExpeditorStockTransferDetailDTO detail =
                stockTransferService.getDetailForExpeditor(filter.getId());

        if (detail == null || detail.getProducts() == null) {
            return workbook;
        }

        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        Row headerRow1 = sheet.createRow(0);
        Row headerRow2 = sheet.createRow(1);

        headerRow1.createCell(0).setCellValue("Товар");
        headerRow1.createCell(1).setCellValue("Штрихкод");
        headerRow1.createCell(2).setCellValue("Передано экспедитору");
        headerRow1.createCell(4).setCellValue("Продано");
        headerRow1.createCell(6).setCellValue("Остаток");

        headerRow2.createCell(2).setCellValue("шт");
        headerRow2.createCell(3).setCellValue("блоки");
        headerRow2.createCell(4).setCellValue("шт");
        headerRow2.createCell(5).setCellValue("блоки");
        headerRow2.createCell(6).setCellValue("шт");

        sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 0)); // Товар
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 1, 1)); // Штрихкод
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 2, 3)); // Передано экспедитору
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 4, 5)); // Продано
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 6, 6)); // Остаток


        int rowIndex = 2;

        for (ExpeditorStockTransferProductDetailDTO product : detail.getProducts()) {

            Row row = sheet.createRow(rowIndex++);

            BigDecimal qty = Optional.ofNullable(product.getQty()).orElse(BigDecimal.ZERO);
            BigDecimal blockQty = Optional.ofNullable(product.getBlockQty()).orElse(BigDecimal.ZERO);
            BigDecimal soldQty = Optional.ofNullable(product.getSoldQty()).orElse(BigDecimal.ZERO);
            BigDecimal soldQtyInBlock = Optional.ofNullable(product.getSoldQtyInBlock()).orElse(BigDecimal.ZERO);
            BigDecimal warehouseQty =Optional.ofNullable(product.getQtyInWarehouse()).orElse(BigDecimal.ZERO);

            row.createCell(0).setCellValue(Optional.ofNullable(product.getName()).orElse(""));
            row.createCell(1).setCellValue(Optional.ofNullable(product.getBarcode()).orElse(""));
            row.createCell(2).setCellValue(qty.toPlainString());
            row.createCell(3).setCellValue(blockQty.toString());
            row.createCell(4).setCellValue(soldQty.toPlainString());
            row.createCell(5).setCellValue(soldQtyInBlock.toPlainString());
            row.createCell(6).setCellValue(warehouseQty.toPlainString());
        }
        for (int i = 0; i <= 6; i++) {
            sheet.autoSizeColumn(i);
        }
        return workbook;
    }

}
