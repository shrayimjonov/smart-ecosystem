package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.handlers.export.csv.ServiceOrderSpecificationCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.ServiceOrderSpecificationExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ServiceOrderSpecificationExportStrategy extends AbstractExportStrategy {

    private final ServiceOrderSpecificationExportExcelHandler serviceOrderSpecificationExportExcelHandler;
    private final ServiceOrderSpecificationCsvHandler serviceOrderSpecificationExportCsvHandler;
    ;

    public ServiceOrderSpecificationExportStrategy(ObjectMapper objectMapper, ServiceOrderSpecificationExportExcelHandler serviceOrderSpecificationExportExcelHandler, ServiceOrderSpecificationCsvHandler serviceOrderSpecificationExportCsvHandler) {
        super(objectMapper);
        this.serviceOrderSpecificationExportExcelHandler = serviceOrderSpecificationExportExcelHandler;
        this.serviceOrderSpecificationExportCsvHandler = serviceOrderSpecificationExportCsvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.SERVICE_ORDER_SPECIFICATION;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        BaseFilter filter = objectMapper.convertValue(filterObject, BaseFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    serviceOrderSpecificationExportExcelHandler.generate(filter),
                    serviceOrderSpecificationExportExcelHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    serviceOrderSpecificationExportCsvHandler.generate(filter),
                    serviceOrderSpecificationExportCsvHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
