package uz.uzkassa.smartpossupply.service.handlers.excel;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.filters.StockFilter;
import uz.uzkassa.smartpossupply.service.CustomMultipartFile;
import uz.uzkassa.smartpossupply.service.handlers.csv.AccountReceivableCsvHandler;
import uz.uzkassa.smartpossupply.service.handlers.excel.export.AccountReceivableExcelHandler;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountReceivableStrategy extends AbstractExportStrategy {

    AccountReceivableExcelHandler excelHandler;
    AccountReceivableCsvHandler csvHandler;

    public AccountReceivableStrategy(AccountReceivableExcelHandler excelHandler, AccountReceivableCsvHandler csvHandler) {
        this.excelHandler = excelHandler;
        this.csvHandler = csvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.ACCOUNT_RECEIVABLE;
    }

    @Override
    public CustomMultipartFile generateReport(Object filter, FileType fileType) {
        StockFilter stockFilter = objectMapper.convertValue(filter, StockFilter.class);
        return switch (fileType) {
            case CSV -> new CustomMultipartFile(
                    csvHandler.generate(stockFilter),
                    csvHandler.getFileName(stockFilter),
                    CONTENT_TYPE_FOR_CSV
            );
            case EXCEL -> new CustomMultipartFile(
                    excelHandler.generate(stockFilter),
                    excelHandler.getFileName(stockFilter),
                    CONTENT_TYPE_FOR_EXCEL
            );
        };
    }
}
