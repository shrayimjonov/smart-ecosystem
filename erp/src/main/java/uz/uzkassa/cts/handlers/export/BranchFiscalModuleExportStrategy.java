package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.handlers.export.csv.BranchFiscalModuleExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.BranchFiscalModuleExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class BranchFiscalModuleExportStrategy extends AbstractExportStrategy {

    BranchFiscalModuleExportExcelHandler branchFiscalModuleExportExcelHandler;
    BranchFiscalModuleExportCsvHandler branchFiscalModuleExportCsvHandler;

    public BranchFiscalModuleExportStrategy(ObjectMapper objectMapper, BranchFiscalModuleExportExcelHandler branchFiscalModuleExportExcelHandler, BranchFiscalModuleExportCsvHandler branchFiscalModuleExportCsvHandler) {
        super(objectMapper);
        this.branchFiscalModuleExportExcelHandler = branchFiscalModuleExportExcelHandler;
        this.branchFiscalModuleExportCsvHandler = branchFiscalModuleExportCsvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.BRANCH_FISCAL_MODULE;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        BaseFilter filter = objectMapper.convertValue(filterObject, BaseFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    branchFiscalModuleExportExcelHandler.generate(filter),
                    branchFiscalModuleExportExcelHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    branchFiscalModuleExportCsvHandler.generate(filter),
                    branchFiscalModuleExportCsvHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
