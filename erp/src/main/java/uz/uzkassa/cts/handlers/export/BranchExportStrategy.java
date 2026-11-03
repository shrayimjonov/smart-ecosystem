package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.handlers.export.csv.BranchExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.BranchExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class BranchExportStrategy extends AbstractExportStrategy {

    BranchExportExcelHandler branchExportExcelHandler;
    BranchExportCsvHandler branchExportCsvHandler;

    public BranchExportStrategy(ObjectMapper objectMapper, BranchExportExcelHandler branchExportExcelHandler, BranchExportCsvHandler branchExportCsvHandler) {
        super(objectMapper);
        this.branchExportExcelHandler = branchExportExcelHandler;
        this.branchExportCsvHandler = branchExportCsvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.BRANCH;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        BaseFilter filter = objectMapper.convertValue(filterObject, BaseFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    branchExportExcelHandler.generate(filter),
                    branchExportExcelHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    branchExportCsvHandler.generate(filter),
                    branchExportCsvHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
