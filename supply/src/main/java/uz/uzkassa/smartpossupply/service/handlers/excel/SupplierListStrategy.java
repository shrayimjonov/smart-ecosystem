package uz.uzkassa.smartpossupply.service.handlers.excel;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.filters.CompanyFilter;
import uz.uzkassa.smartpossupply.service.CustomMultipartFile;
import uz.uzkassa.smartpossupply.service.handlers.csv.SupplierListCsvHandler;
import uz.uzkassa.smartpossupply.service.handlers.excel.export.SupplierListExcelHandler;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SupplierListStrategy extends AbstractExportStrategy {

    SupplierListExcelHandler excelHandler;
    SupplierListCsvHandler csvHandler;

    public SupplierListStrategy(SupplierListExcelHandler excelHandler, SupplierListCsvHandler csvHandler) {
        this.excelHandler = excelHandler;
        this.csvHandler = csvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.SUPPLIER;
    }

    @Override
    public CustomMultipartFile generateReport(Object filter, FileType fileType) {
        CompanyFilter supplierFilter = objectMapper.convertValue(filter, CompanyFilter.class);
        return switch (fileType) {
            case CSV -> new CustomMultipartFile(
                    csvHandler.generate(supplierFilter),
                    csvHandler.getFileName(supplierFilter),
                    CONTENT_TYPE_FOR_CSV
            );
            case EXCEL -> new CustomMultipartFile(
                    excelHandler.generate(supplierFilter),
                    excelHandler.getFileName(supplierFilter),
                    CONTENT_TYPE_FOR_EXCEL
            );
        };
    }
}
