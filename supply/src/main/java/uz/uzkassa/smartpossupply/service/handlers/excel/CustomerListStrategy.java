package uz.uzkassa.smartpossupply.service.handlers.excel;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.filters.CompanyFilter;
import uz.uzkassa.smartpossupply.service.CustomMultipartFile;
import uz.uzkassa.smartpossupply.service.handlers.csv.CustomerListCsvHandler;
import uz.uzkassa.smartpossupply.service.handlers.excel.export.CustomerListExcelHandler;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerListStrategy extends AbstractExportStrategy {

    CustomerListExcelHandler excelHandler;
    CustomerListCsvHandler csvHandler;

    public CustomerListStrategy(CustomerListExcelHandler excelHandler, CustomerListCsvHandler csvHandler) {
        this.excelHandler = excelHandler;
        this.csvHandler = csvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.CUSTOMER;
    }

    @Override
    public CustomMultipartFile generateReport(Object filter, FileType fileType) {
        CompanyFilter customerFilter = objectMapper.convertValue(filter, CompanyFilter.class);
        return switch (fileType) {
            case CSV -> new CustomMultipartFile(
                    csvHandler.generate(customerFilter),
                    csvHandler.getFileName(customerFilter),
                    CONTENT_TYPE_FOR_CSV
            );
            case EXCEL -> new CustomMultipartFile(
                    excelHandler.generate(customerFilter),
                    excelHandler.getFileName(customerFilter),
                    CONTENT_TYPE_FOR_EXCEL
            );
        };
    }
}
