package uz.uzkassa.smartpossupply.service.handlers.excel;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.filters.MaterialReportFilter;
import uz.uzkassa.smartpossupply.service.CustomMultipartFile;
import uz.uzkassa.smartpossupply.service.handlers.excel.export.MaterialReportExcelHandler;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MaterialReportStrategy extends AbstractExportStrategy {

    MaterialReportExcelHandler excelHandler;

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.MATERIAL_REPORT;
    }

    @Override
    public CustomMultipartFile generateReport(Object filter, FileType fileType) {
        MaterialReportFilter materialReportFilter = objectMapper.convertValue(filter, MaterialReportFilter.class);
        return switch (fileType) {
            case CSV -> null;
            case EXCEL -> new CustomMultipartFile(
                    excelHandler.generate(materialReportFilter),
                    excelHandler.getFileName(materialReportFilter),
                    CONTENT_TYPE_FOR_EXCEL
            );
        };
    }
}
