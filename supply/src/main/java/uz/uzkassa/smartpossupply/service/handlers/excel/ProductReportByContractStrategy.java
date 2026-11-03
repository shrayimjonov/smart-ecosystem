package uz.uzkassa.smartpossupply.service.handlers.excel;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.filters.BalanceReportFilter;
import uz.uzkassa.smartpossupply.service.CustomMultipartFile;
import uz.uzkassa.smartpossupply.service.handlers.csv.ProductReportByContractCsvHandler;
import uz.uzkassa.smartpossupply.service.handlers.excel.export.ProductReportByContractExcelHandler;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductReportByContractStrategy extends AbstractExportStrategy {

    ProductReportByContractExcelHandler excelHandler;
    ProductReportByContractCsvHandler csvHandler;

    public ProductReportByContractStrategy(ProductReportByContractExcelHandler excelHandler, ProductReportByContractCsvHandler csvHandler) {
        this.excelHandler = excelHandler;
        this.csvHandler = csvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.PRODUCT_REPORT_BY_CONTRACT;
    }

    @Override
    public CustomMultipartFile generateReport(Object filter, FileType fileType) {
        BalanceReportFilter balanceReportFilter = objectMapper.convertValue(filter, BalanceReportFilter.class);
        return switch (fileType) {
            case CSV -> new CustomMultipartFile(
                    csvHandler.generate(balanceReportFilter),
                    csvHandler.getFileName(balanceReportFilter),
                    CONTENT_TYPE_FOR_CSV
            );
            case EXCEL -> new CustomMultipartFile(
                    excelHandler.generate(balanceReportFilter),
                    excelHandler.getFileName(balanceReportFilter),
                    CONTENT_TYPE_FOR_EXCEL
            );
        };
    }
}
