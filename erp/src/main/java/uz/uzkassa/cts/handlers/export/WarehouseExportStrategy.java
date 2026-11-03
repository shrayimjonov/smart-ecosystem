package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.handlers.export.csv.WarehouseExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.WarehouseExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class WarehouseExportStrategy extends AbstractExportStrategy {

    WarehouseExportExcelHandler warehouseExportExcelHandler;
    WarehouseExportCsvHandler warehouseExportCsvHandler;

    public WarehouseExportStrategy(ObjectMapper objectMapper, WarehouseExportExcelHandler warehouseExportExcelHandler, WarehouseExportCsvHandler warehouseExportCsvHandler) {
        super(objectMapper);
        this.warehouseExportExcelHandler = warehouseExportExcelHandler;
        this.warehouseExportCsvHandler = warehouseExportCsvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.WAREHOUSE;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        BaseFilter filter = objectMapper.convertValue(filterObject, BaseFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    warehouseExportExcelHandler.generate(filter),
                    warehouseExportExcelHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    warehouseExportCsvHandler.generate(filter),
                    warehouseExportCsvHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
