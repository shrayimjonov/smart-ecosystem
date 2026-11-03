package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.OfdApplicationFilter;
import uz.uzkassa.cts.handlers.export.csv.OfdApplicationExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.OfdApplicationExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class OfdApplicationExportStrategy extends AbstractExportStrategy {

    OfdApplicationExportExcelHandler ofdApplicationExcelHandler;
    OfdApplicationExportCsvHandler ofdApplicationCsvHandler;

    public OfdApplicationExportStrategy(ObjectMapper objectMapper, OfdApplicationExportExcelHandler ofdApplicationExcelHandler, OfdApplicationExportCsvHandler ofdApplicationCsvHandler) {
        super(objectMapper);
        this.ofdApplicationExcelHandler = ofdApplicationExcelHandler;
        this.ofdApplicationCsvHandler = ofdApplicationCsvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.OFD_APPLICATION;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        OfdApplicationFilter filter = objectMapper.convertValue(filterObject, OfdApplicationFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    ofdApplicationExcelHandler.generate(filter),
                    ofdApplicationExcelHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    ofdApplicationCsvHandler.generate(filter),
                    ofdApplicationCsvHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
