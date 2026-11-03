package uz.uzkassa.cts.handlers.export.excel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.inventory.Warehouse;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.WarehouseRepository;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class WarehouseExportExcelHandler extends AbstractExportExcelHandler<BaseFilter> {

    WarehouseRepository warehouseRepository;

    @Override
    public String getFileName(BaseFilter filter) {
        return "Warehouses.xlsx";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Название", "Компания", "Филиал", "Регион", "Район", "Менеджер", "Статус");
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(BaseFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        filter.setPage(0);
        filter.setSize(EXCEL_EXPORT_MAX_LIMIT);
        List<Warehouse> result = warehouseRepository.getResultList(filter).getList();
        int rowIndex = 0;
        for (Warehouse warehouse : result) {
            Row row = sheet.createRow(++rowIndex);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(warehouse.getName());
            row.createCell(cellIndex++).setCellValue(warehouse.getBranch().getCompany().getName());
            row.createCell(cellIndex++).setCellValue(warehouse.getBranch().getName());
            row.createCell(cellIndex++).setCellValue(warehouse.getAddress().getRegion().getName());
            row.createCell(cellIndex++).setCellValue(warehouse.getAddress().getDistrict().getName());
            row.createCell(cellIndex++).setCellValue(warehouse.getManager().getFullName());
            row.createCell(cellIndex).setCellValue(warehouse.getStatus().getName());
        }
        return workbook;
    }
}
