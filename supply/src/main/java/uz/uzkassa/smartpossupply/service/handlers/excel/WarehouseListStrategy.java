package uz.uzkassa.smartpossupply.service.handlers.excel;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.service.CustomMultipartFile;
import uz.uzkassa.smartpossupply.service.handlers.csv.WarehouseListCsvHandler;
import uz.uzkassa.smartpossupply.service.handlers.excel.export.WarehouseListExcelHandler;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WarehouseListStrategy extends AbstractExportStrategy {

    WarehouseListExcelHandler excelHandler;
    WarehouseListCsvHandler csvHandler;

    public WarehouseListStrategy(WarehouseListExcelHandler excelHandler, WarehouseListCsvHandler csvHandler) {
        this.excelHandler = excelHandler;
        this.csvHandler = csvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.WAREHOUSE;
    }

    @Override
    public CustomMultipartFile generateReport(Object filter, FileType fileType) {
        BaseFilter warehouseFilter = objectMapper.convertValue(filter, BaseFilter.class);
        return switch (fileType) {
            case CSV -> new CustomMultipartFile(
                    csvHandler.generate(warehouseFilter),
                    csvHandler.getFileName(warehouseFilter),
                    CONTENT_TYPE_FOR_CSV
            );
            case EXCEL -> new CustomMultipartFile(
                    excelHandler.generate(warehouseFilter),
                    excelHandler.getFileName(warehouseFilter),
                    CONTENT_TYPE_FOR_EXCEL
            );
        };
    }
}
