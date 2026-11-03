package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.handlers.export.csv.BankExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.BankExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class BankExportStrategy extends AbstractExportStrategy {

    BankExportExcelHandler bankExportExcelHandler;
    BankExportCsvHandler bankExportCsvHandler;

    public BankExportStrategy(ObjectMapper objectMapper, BankExportExcelHandler bankExportExcelHandler, BankExportCsvHandler bankExportCsvHandler) {
        super(objectMapper);
        this.bankExportExcelHandler = bankExportExcelHandler;
        this.bankExportCsvHandler = bankExportCsvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.BANK;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        BaseFilter filter = objectMapper.convertValue(filterObject, BaseFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    bankExportExcelHandler.generate(filter),
                    bankExportExcelHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    bankExportCsvHandler.generate(filter),
                    bankExportCsvHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
