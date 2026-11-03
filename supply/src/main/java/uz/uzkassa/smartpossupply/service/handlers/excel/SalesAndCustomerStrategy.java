package uz.uzkassa.smartpossupply.service.handlers.excel;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.filters.OrderFilter;
import uz.uzkassa.smartpossupply.service.CustomMultipartFile;
import uz.uzkassa.smartpossupply.service.handlers.excel.export.SalesAndCustomerExcelHandler;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SalesAndCustomerStrategy extends AbstractExportStrategy {

    SalesAndCustomerExcelHandler excelHandler;

    public SalesAndCustomerStrategy(SalesAndCustomerExcelHandler excelHandler) {
        this.excelHandler = excelHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.SALES_CUSTOMER_REPORT;
    }

    @Override
    public CustomMultipartFile generateReport(Object filter, FileType fileType) {
        uz.uzkassa.smartpossupply.filters.OrderFilter orderFilter = objectMapper.convertValue(filter, OrderFilter.class);
        return switch (fileType) {
            case EXCEL ->
                    new CustomMultipartFile(excelHandler.generate(orderFilter), excelHandler.getFileName(orderFilter), CONTENT_TYPE_FOR_EXCEL);
            case CSV -> throw new BadRequestException("CSV not supported");
        };
    }
}
