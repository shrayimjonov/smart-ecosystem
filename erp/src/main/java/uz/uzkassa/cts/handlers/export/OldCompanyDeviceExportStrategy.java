package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.CompanyDeviceListFilter;
import uz.uzkassa.cts.handlers.export.excel.OldCompanyDeviceExportExcelHandler;
import uz.uzkassa.cts.service.ValidationService;

import static lombok.AccessLevel.PRIVATE;
import static uz.uzkassa.cts.constants.MessageConstants.INVALID_CONTENT_TYPE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class OldCompanyDeviceExportStrategy extends AbstractExportStrategy {

    OldCompanyDeviceExportExcelHandler oldCompanyDeviceExportExcelHandler;
    ValidationService validationService;

    public OldCompanyDeviceExportStrategy(ObjectMapper objectMapper, OldCompanyDeviceExportExcelHandler oldCompanyDeviceExportExcelHandler, ValidationService validationService) {
        super(objectMapper);
        this.oldCompanyDeviceExportExcelHandler = oldCompanyDeviceExportExcelHandler;
        this.validationService = validationService;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.OLD_COMPANY_DEVICE;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        CompanyDeviceListFilter filter = objectMapper.convertValue(filterObject, CompanyDeviceListFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    oldCompanyDeviceExportExcelHandler.generate(filter),
                    oldCompanyDeviceExportExcelHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            default -> throw validationService.badRequestExceptionThrow(INVALID_CONTENT_TYPE).get();
        };
    }
}
