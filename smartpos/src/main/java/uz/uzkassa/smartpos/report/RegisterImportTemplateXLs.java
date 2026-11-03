package uz.uzkassa.smartpos.report;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.dto.ReportDTO;
import uz.uzkassa.smartpos.enums.ProductFileHeader;
import uz.uzkassa.smartpos.exceptions.EntityNotFound;
import uz.uzkassa.smartpos.repository.postgres.company.BranchRepository;
import uz.uzkassa.smartpos.repository.postgres.warehouse.RegisterRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Lazy
public class RegisterImportTemplateXLs extends AbstractExcelReport {

    private final RegisterRepository registerRepository;

    private static final String sheetName = "Отчет";


    public RegisterImportTemplateXLs(BranchRepository branchRepository, RegisterRepository registerRepository) {
        this.branchRepository = branchRepository;
        this.registerRepository = registerRepository;
    }


    public ByteArrayResource exportRegisters(Long branchId) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); var bos = new ByteArrayOutputStream()) {
            XSSFSheet sheet = workbook.createSheet(sheetName);
            createHeader(workbook, sheet, new String[]{
                    "ID",
                    ProductFileHeader.CATEGORY.getNameRu(),
                    ProductFileHeader.PRODUCT_NAME.getNameRu(),
                    ProductFileHeader.BARCODE.getNameRu(),
                    ProductFileHeader.VAT_BARCODE.getNameRu(),
                    ProductFileHeader.UNIT.getNameRu(),
                    ProductFileHeader.PRICE.getNameRu(),
                    ProductFileHeader.BALANCE.getNameRu()
            }, 0);
            List<ReportDTO> registers = new ArrayList<>();
            if (branchId != null) {
                Branch branch = branchRepository.findById(branchId).orElseThrow(() -> new EntityNotFound(Branch.class.getSimpleName(), branchId));
                registers = registerRepository.findAllByBranchIdAndCityId(branch.getId(), branch.getCityKeyId());
            }
            insertRows(sheet, registers);
            workbook.write(bos);
            return new ByteArrayResource(bos.toByteArray());
        }

    }

    public void insertRows(XSSFSheet sheet, List<ReportDTO> registers) {
        Row row;
        int i = 1;
        for (ReportDTO product : registers) {
            row = sheet.createRow(i++);
            row.createCell(0).setCellValue(product.getRegisterId().toString());
            row.createCell(1).setCellValue(product.getCategoryName());
            row.createCell(2).setCellValue(product.getProductName());
            row.createCell(3).setCellValue(product.getBarcode());
            row.createCell(4).setCellValue(product.getVatBarcode());
            row.createCell(5).setCellValue(product.getUnitName());
            row.createCell(6).setCellValue(product.getSalesPrice() == null ? "" : product.getSalesPrice().toString());
            row.createCell(7).setCellValue(product.getBalance() == null ? "" : product.getBalance().toString());
        }
    }
}

