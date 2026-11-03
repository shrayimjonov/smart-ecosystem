package uz.uzkassa.smartpos.report;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpos.domain.postgres.catalog.CategoryBranch;
import uz.uzkassa.smartpos.domain.postgres.catalog.CategoryBranchId;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductBranch;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.enums.ProductFileHeader;
import uz.uzkassa.smartpos.exceptions.EntityNotFound;
import uz.uzkassa.smartpos.repository.postgres.catalog.CategoryBranchRepository;
import uz.uzkassa.smartpos.repository.postgres.catalog.ProductBranchRepository;
import uz.uzkassa.smartpos.repository.postgres.company.BranchRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Lazy
public class CatalogProductImportTemplateXLs extends AbstractExcelReport {

    private final ProductBranchRepository productBranchRepository;

    private static final String sheetName = "Продукты";
    private final CategoryBranchRepository categoryBranchRepository;


    public CatalogProductImportTemplateXLs(ProductBranchRepository productBranchRepository, BranchRepository branchRepository, CategoryBranchRepository categoryBranchRepository) {
        this.productBranchRepository = productBranchRepository;
        this.branchRepository = branchRepository;
        this.categoryBranchRepository = categoryBranchRepository;
    }


    public ByteArrayResource exportProducts(Long categoryId, Long branchId) throws IOException {
        try (var workbook = new XSSFWorkbook(); var bos = new ByteArrayOutputStream()) {
            XSSFSheet sheet = workbook.createSheet(sheetName);
            createHeader(workbook, sheet, new String[]{
                    "ID",
                    ProductFileHeader.CATEGORY.getNameRu(),
                    ProductFileHeader.PRODUCT_NAME.getNameRu(),
                    ProductFileHeader.BARCODE.getNameRu(),
                    ProductFileHeader.VAT_BARCODE.getNameRu(),
                    ProductFileHeader.UNIT.getNameRu(),
                    ProductFileHeader.FAVORITE.getNameRu(),
                    ProductFileHeader.MARK.getNameRu(),
                    ProductFileHeader.PRICE.getNameRu(),
                    ProductFileHeader.VAT.getNameRu()
            }, 0);
            List<ProductBranch> products = new ArrayList<>();
            if (categoryId != null && branchId != null) {
                Branch branch = branchRepository.findById(branchId).orElseThrow(() -> new EntityNotFound(Branch.class.getSimpleName(), branchId));
                CategoryBranch category = categoryBranchRepository.getReferenceById(new CategoryBranchId(categoryId, branchId));
                var categoryIds = new ArrayList<Long>();
                categoryIds.add(categoryId);
                List<CategoryBranch> childCategories = category.getChildCategories();
                while (!childCategories.isEmpty()) {
                    childCategories.forEach(childCategory -> categoryIds.add(childCategory.getCategoryId()));
                    childCategories = childCategories.stream().flatMap(childCategory -> childCategory.getChildCategories().stream())
                            .toList();
                }
                int page = 0;
                Page<ProductBranch> productBranchPage = productBranchRepository.findAllByCityIdAndCategoryIdInAndBranchIdAndDeletedIsFalseOrderByProductId(branch.getCityKeyId(), categoryIds, branchId, PageRequest.of(page++, 500));
                while (!productBranchPage.isEmpty()) {
                    products.addAll(productBranchPage.getContent());
                    productBranchPage = productBranchRepository.findAllByCityIdAndCategoryIdInAndBranchIdAndDeletedIsFalseOrderByProductId(branch.getCityKeyId(), categoryIds, branchId, PageRequest.of(page++, 500));
                }
            }
            insertRows(sheet, products);
            workbook.write(bos);
            return new ByteArrayResource(bos.toByteArray());
        }

    }

    public void insertRows(XSSFSheet sheet, List<ProductBranch> products) {
        Row row;
        int i = 1;
        for (ProductBranch product : products) {
            row = sheet.createRow(i++);
            row.createCell(0).setCellValue(product.getProductId());
            row.createCell(1).setCellValue(product.getCategory().getName());
            row.createCell(2).setCellValue(product.getName());
            row.createCell(3).setCellValue(product.getBarcode());
            row.createCell(4).setCellValue(product.getVatBarcode());
            row.createCell(5).setCellValue(product.getUnit().getDescription().toLowerCase());
            row.createCell(6).setCellValue(product.isFavorite() ? "Да" : "Нет");
            row.createCell(7).setCellValue(product.isHasMark() ? "Да" : "Нет");
            row.createCell(8).setCellValue(product.getSalesPrice() == null ? "" : product.getSalesPrice().toString());
            row.createCell(9).setCellValue(product.getVatRate() == null ? "Без НДС" : product.getVatRate().toString());
        }
    }
}

