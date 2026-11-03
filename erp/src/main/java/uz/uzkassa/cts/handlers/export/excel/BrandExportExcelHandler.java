package uz.uzkassa.cts.handlers.export.excel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.inventory.Brand;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.BrandRepository;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class BrandExportExcelHandler extends AbstractExportExcelHandler<BaseFilter> {

    BrandRepository brandRepository;

    @Override
    public String getFileName(BaseFilter filter) {
        return "Brands.xlsx";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Название");
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(BaseFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        filter.setPage(0);
        filter.setSize(EXCEL_EXPORT_MAX_LIMIT);
        List<Brand> result = brandRepository.getResultList(filter).getList();
        int rowIndex = 0;
        for (Brand brand : result) {
            Row row = sheet.createRow(++rowIndex);
            int cellIndex = 0;
            row.createCell(cellIndex).setCellValue(brand.getName());
        }
        return workbook;
    }
}
