package uz.uzkassa.smartpossupply.service.handlers.excel.export;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.supplier.SupplierListDTO;
import uz.uzkassa.smartpossupply.filters.CompanyFilter;
import uz.uzkassa.smartpossupply.service.SupplierService;

import java.util.LinkedList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SupplierListExcelHandler extends AbstractExcelHandler<CompanyFilter> {

    SupplierService supplierService;

    @Override
    public String getFileName(CompanyFilter filter) {
        return "Suppliers.xlsx";
    }

    @Override
    public LinkedList<String> getHeaders() {
        LinkedList<String> header = new LinkedList<>();
        header.add("ИНН");
        header.add("Название");
        header.add("Регион");
        header.add("Район");
        header.add("Адрес");
        header.add("Контактное лицо");
        return header;
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(CompanyFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        List<SupplierListDTO> suppliers = supplierService.getList(filter).getContent();
        int rowIndex = 0;
        for (SupplierListDTO supplier : suppliers) {
            Row row = sheet.createRow(++rowIndex);
            row.createCell(0).setCellValue(supplier.getTin());
            row.createCell(1).setCellValue(supplier.getName());
            row.createCell(2).setCellValue(supplier.getAddress() != null && supplier.getAddress().getRegion() != null ? supplier.getAddress().getRegion().getName() : "");
            row.createCell(3).setCellValue(supplier.getAddress() != null && supplier.getAddress().getDistrict() != null ? supplier.getAddress().getDistrict().getName() : "");
            row.createCell(4).setCellValue(supplier.getAddress() != null ? supplier.getAddress().getFullAddress() : "");
            row.createCell(5).setCellValue(supplier.getContactPerson());
        }
        return workbook;
    }
}
