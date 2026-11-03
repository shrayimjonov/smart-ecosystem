package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.handlers.export.csv.BrandExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.BrandExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class BrandExportStrategy extends AbstractExportStrategy {

    BrandExportExcelHandler brandExportExcelHandler;
    BrandExportCsvHandler brandExportCsvHandler;

    public BrandExportStrategy(ObjectMapper objectMapper, BrandExportExcelHandler branchExportExcelHandler, BrandExportCsvHandler branchExportCsvHandler) {
        super(objectMapper);
        this.brandExportExcelHandler = branchExportExcelHandler;
        this.brandExportCsvHandler = branchExportCsvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.BRAND;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        BaseFilter filter = objectMapper.convertValue(filterObject, BaseFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    brandExportExcelHandler.generate(filter),
                    brandExportExcelHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    brandExportCsvHandler.generate(filter),
                    brandExportCsvHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
