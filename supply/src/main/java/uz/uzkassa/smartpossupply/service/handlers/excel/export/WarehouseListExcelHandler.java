package uz.uzkassa.smartpossupply.service.handlers.excel.export;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.warehouse.WarehouseListDTO;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.service.WarehouseService;

import java.util.LinkedList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 06.12.2022 18:57
 */
@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class WarehouseListExcelHandler extends AbstractExcelHandler<BaseFilter> {

    WarehouseService warehouseService;

    @Override
    public String getFileName(BaseFilter filter) {
        return "Warehouses.xlsx";
    }

    @Override
    public LinkedList<String> getHeaders() {
        LinkedList<String> header = new LinkedList<>();
        header.add("Название");
        header.add("Филиалы");
        header.add("Ответственное лицо");
        header.add("Основной");
        header.add("Статус");
        return header;
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(BaseFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        List<WarehouseListDTO> warehouses = warehouseService.getList(filter).getContent();
        int rowIndex = 0;
        for (WarehouseListDTO warehouse : warehouses) {
            Row row = sheet.createRow(++rowIndex);
            row.createCell(0).setCellValue(warehouse.getName());
            row.createCell(1).setCellValue(warehouse.getBranch() != null ? warehouse.getBranch().getName() : "");
            row.createCell(2).setCellValue(warehouse.getOwner() != null ? (warehouse.getOwner().getFirstName() + " " + warehouse.getOwner().getLastName()) : "");
            row.createCell(3).setCellValue(warehouse.isMain() ? "Да" : "Нет");
            row.createCell(4).setCellValue(warehouse.getStatus() != null ? warehouse.getStatus().getName() : "");
        }
        return workbook;
    }
}
