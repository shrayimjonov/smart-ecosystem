package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.handlers.export.csv.BranchDeviceExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.BranchDeviceExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class BranchDeviceExportStrategy extends AbstractExportStrategy {

    BranchDeviceExportExcelHandler branchDeviceExportExcelHandler;
    BranchDeviceExportCsvHandler branchDeviceExportCsvHandler;

    public BranchDeviceExportStrategy(ObjectMapper objectMapper, BranchDeviceExportExcelHandler branchDeviceExportExcelHandler, BranchDeviceExportCsvHandler branchDeviceExportCsvHandler) {
        super(objectMapper);
        this.branchDeviceExportExcelHandler = branchDeviceExportExcelHandler;
        this.branchDeviceExportCsvHandler = branchDeviceExportCsvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.BRANCH_DEVICE;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        BaseFilter filter = objectMapper.convertValue(filterObject, BaseFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    branchDeviceExportExcelHandler.generate(filter),
                    branchDeviceExportExcelHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    branchDeviceExportCsvHandler.generate(filter),
                    branchDeviceExportCsvHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
