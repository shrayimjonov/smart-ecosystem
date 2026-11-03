package uz.uzkassa.smartpossupply.service.handlers.excel;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.filters.FinancialOperationFilter;
import uz.uzkassa.smartpossupply.service.CustomMultipartFile;
import uz.uzkassa.smartpossupply.service.handlers.csv.FinancialOperationListCsvHandler;
import uz.uzkassa.smartpossupply.service.handlers.excel.export.FinancialOperationListExcelHandler;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FinancialOperationListStrategy extends AbstractExportStrategy {

    FinancialOperationListExcelHandler excelHandler;
    FinancialOperationListCsvHandler csvHandler;

    public FinancialOperationListStrategy(FinancialOperationListExcelHandler excelHandler, FinancialOperationListCsvHandler csvHandler) {
        this.excelHandler = excelHandler;
        this.csvHandler = csvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.FINANCIAL_OPERATION;
    }

    @Override
    public CustomMultipartFile generateReport(Object filter, FileType fileType) {
        FinancialOperationFilter financialOperationFilter = objectMapper.convertValue(filter, FinancialOperationFilter.class);
        return switch (fileType) {
            case CSV -> new CustomMultipartFile(
                    csvHandler.generate(financialOperationFilter),
                    csvHandler.getFileName(financialOperationFilter),
                    CONTENT_TYPE_FOR_CSV
            );
            case EXCEL -> new CustomMultipartFile(
                    excelHandler.generate(financialOperationFilter),
                    excelHandler.getFileName(financialOperationFilter),
                    CONTENT_TYPE_FOR_EXCEL
            );
        };
    }
}
