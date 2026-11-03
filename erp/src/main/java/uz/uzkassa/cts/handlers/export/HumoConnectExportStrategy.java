package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.HumoConnectFilter;
import uz.uzkassa.cts.handlers.export.csv.HumoConnectExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.HumoConnectExportExcelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class HumoConnectExportStrategy extends AbstractExportStrategy {

    HumoConnectExportExcelHandler humoConnectExportExcelHandler;
    HumoConnectExportCsvHandler humoConnectExportCsvHandler;

    public HumoConnectExportStrategy(ObjectMapper objectMapper, HumoConnectExportExcelHandler humoConnectExportExcelHandler, HumoConnectExportCsvHandler humoConnectExportCsvHandler) {
        super(objectMapper);
        this.humoConnectExportExcelHandler = humoConnectExportExcelHandler;
        this.humoConnectExportCsvHandler = humoConnectExportCsvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.HUMO_CONNECT;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        HumoConnectFilter filter = objectMapper.convertValue(filterObject, HumoConnectFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    humoConnectExportExcelHandler.generate(filter),
                    humoConnectExportExcelHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    humoConnectExportCsvHandler.generate(filter),
                    humoConnectExportCsvHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
