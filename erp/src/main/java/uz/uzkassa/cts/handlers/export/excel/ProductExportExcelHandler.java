package uz.uzkassa.cts.handlers.export.excel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.inventory.Product;
import uz.uzkassa.cts.filter.ProductFilter;
import uz.uzkassa.cts.repository.ProductRepository;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ProductExportExcelHandler extends AbstractExportExcelHandler<ProductFilter> {

    ProductRepository productRepository;

    @Override
    public String getFileName(ProductFilter filter) {
        return "Products.xlsx";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Товар", "Категория", "НДС", "Ед. изм.", "Цена");
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(ProductFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        filter.setPage(0);
        filter.setSize(EXCEL_EXPORT_MAX_LIMIT);
        List<Product> result = productRepository.getResultList(filter).getList();
        int rowIndex = 0;
        for (Product product : result) {
            Row row = sheet.createRow(++rowIndex);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(product.getName());
            row.createCell(cellIndex++).setCellValue(product.getCategory().getName());
            row.createCell(cellIndex++).setCellValue(product.getVat().getName());
            row.createCell(cellIndex++).setCellValue(product.getUnit().getName());
            row.createCell(cellIndex).setCellValue(product.getPrice().toString());
        }
        return workbook;
    }
}
