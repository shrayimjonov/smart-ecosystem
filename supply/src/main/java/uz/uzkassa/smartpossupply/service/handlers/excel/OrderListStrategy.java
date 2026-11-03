package uz.uzkassa.smartpossupply.service.handlers.excel;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.filters.OrderFilter;
import uz.uzkassa.smartpossupply.service.CustomMultipartFile;
import uz.uzkassa.smartpossupply.service.handlers.csv.OrderListCsvHandler;
import uz.uzkassa.smartpossupply.service.handlers.excel.export.OrderListExcelHandler;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderListStrategy extends AbstractExportStrategy {

    OrderListExcelHandler excelHandler;
    OrderListCsvHandler csvHandler;

    public OrderListStrategy(OrderListExcelHandler excelHandler, OrderListCsvHandler csvHandler) {
        this.excelHandler = excelHandler;
        this.csvHandler = csvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.ORDER;
    }

    @Override
    public CustomMultipartFile generateReport(Object filter, FileType fileType) {
        OrderFilter orderFilter = objectMapper.convertValue(filter, OrderFilter.class);
        return switch (fileType) {
            case CSV -> new CustomMultipartFile(
                    csvHandler.generate(orderFilter),
                    csvHandler.getFileName(orderFilter),
                    CONTENT_TYPE_FOR_CSV
            );
            case EXCEL -> new CustomMultipartFile(
                    excelHandler.generate(orderFilter),
                    excelHandler.getFileName(orderFilter),
                    CONTENT_TYPE_FOR_EXCEL
            );
        };
    }
}
