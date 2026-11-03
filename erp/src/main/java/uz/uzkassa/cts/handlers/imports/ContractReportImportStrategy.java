package uz.uzkassa.cts.handlers.imports;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.dto.payload.ImportReportItemPayload;
import uz.uzkassa.cts.dto.payload.ImportReportPayload;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.handlers.imports.excel.ContractReportImportExcelHandler;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * Created date time: 18.03.2025 16:19
 */

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ContractReportImportStrategy implements ImportStrategy {

    ContractReportImportExcelHandler contractReportImportExcelHandler;

    public ContractReportImportStrategy(ContractReportImportExcelHandler contractReportImportExcelHandler) {
        this.contractReportImportExcelHandler = contractReportImportExcelHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.CONTRACT_REPORT;
    }

    @Override
    public List<ImportReportItemPayload> getItems(MultipartFile file, ContentType contentType) {
        if (ContentType.EXCEL == contentType) {
            return contractReportImportExcelHandler.readFile(file);
        }
        return null;
    }

    @Override
    public void importReportQueue(ImportReportPayload payload) {
        contractReportImportExcelHandler.importReportQueue(payload);
    }
}
