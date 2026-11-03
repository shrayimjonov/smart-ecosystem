package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.ProductFilter;
import uz.uzkassa.cts.handlers.export.csv.ProductExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.ProductExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ProductExportStrategy extends AbstractExportStrategy {

    ProductExportExcelHandler productExportExcelHandler;
    ProductExportCsvHandler productExportCsvHandler;

    public ProductExportStrategy(ObjectMapper objectMapper, ProductExportExcelHandler productExportExcelHandler, ProductExportCsvHandler productExportCsvHandler) {
        super(objectMapper);
        this.productExportExcelHandler = productExportExcelHandler;
        this.productExportCsvHandler = productExportCsvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.PRODUCT;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        ProductFilter filter = objectMapper.convertValue(filterObject, ProductFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    productExportExcelHandler.generate(filter),
                    productExportExcelHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    productExportCsvHandler.generate(filter),
                    productExportCsvHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
