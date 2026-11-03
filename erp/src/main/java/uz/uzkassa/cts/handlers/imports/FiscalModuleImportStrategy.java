package uz.uzkassa.cts.handlers.imports;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.dto.payload.ImportReportItemPayload;
import uz.uzkassa.cts.dto.payload.ImportReportPayload;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.handlers.imports.excel.FiscalModuleImportExcelHandler;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class FiscalModuleImportStrategy implements ImportStrategy {

    FiscalModuleImportExcelHandler fiscalModuleImportExcelHandler;

    public FiscalModuleImportStrategy(FiscalModuleImportExcelHandler fiscalModuleImportExcelHandler) {
        this.fiscalModuleImportExcelHandler = fiscalModuleImportExcelHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.FISCAL_MODULE;
    }

    @Override
    public List<ImportReportItemPayload> getItems(MultipartFile file, ContentType contentType) {
        if (ContentType.EXCEL == contentType) {
            return fiscalModuleImportExcelHandler.readFile(file);
        }
        return null;
    }

    @Override
    public void importReportQueue(ImportReportPayload payload) {
        fiscalModuleImportExcelHandler.importReportQueue(payload);
    }
}
