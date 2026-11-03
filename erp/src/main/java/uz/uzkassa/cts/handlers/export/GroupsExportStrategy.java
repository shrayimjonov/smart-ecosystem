package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.handlers.export.csv.GroupExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.GroupExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class GroupsExportStrategy extends AbstractExportStrategy {

    GroupExportExcelHandler groupExcelExportHandler;
    GroupExportCsvHandler groupExportCsvHandler;

    public GroupsExportStrategy(ObjectMapper objectMapper, GroupExportExcelHandler groupExcelExportHandler, GroupExportCsvHandler groupExportCsvHandler) {
        super(objectMapper);
        this.groupExcelExportHandler = groupExcelExportHandler;
        this.groupExportCsvHandler = groupExportCsvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.GROUPS;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        BaseFilter filter = objectMapper.convertValue(filterObject, BaseFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    groupExcelExportHandler.generate(filter),
                    groupExcelExportHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    groupExportCsvHandler.generate(filter),
                    groupExportCsvHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
