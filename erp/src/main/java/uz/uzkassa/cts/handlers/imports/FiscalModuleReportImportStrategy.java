package uz.uzkassa.cts.handlers.imports;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.dto.payload.ImportReportItemPayload;
import uz.uzkassa.cts.dto.payload.ImportReportPayload;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.handlers.imports.excel.FiscalModuleReportImportExcelHandler;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;


@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class FiscalModuleReportImportStrategy implements ImportStrategy {

    FiscalModuleReportImportExcelHandler fiscalModuleReportImportExcelHandler;

    public FiscalModuleReportImportStrategy(FiscalModuleReportImportExcelHandler fiscalModuleReportImportExcelHandler) {
        this.fiscalModuleReportImportExcelHandler = fiscalModuleReportImportExcelHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.FISCAL_MODULE_REPORT;
    }

    @Override
    public List<ImportReportItemPayload> getItems(MultipartFile file, ContentType contentType) {
        if (ContentType.EXCEL == contentType) {
            return fiscalModuleReportImportExcelHandler.readFile(file);
        }
        return null;
    }

    @Override
    public void importReportQueue(ImportReportPayload payload) {
        fiscalModuleReportImportExcelHandler.importReportQueue(payload);
    }
}
