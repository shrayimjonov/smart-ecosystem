package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.RepairTicketFilter;
import uz.uzkassa.cts.handlers.export.csv.RepairTicketExportCsvHandler;
import uz.uzkassa.cts.handlers.export.excel.RepairTicketExportExelHandler;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class RepairTicketExportStrategy extends AbstractExportStrategy {

    RepairTicketExportExelHandler repairTicketExportExelHandler;
    RepairTicketExportCsvHandler repairTicketExportCsvHandler;

    public RepairTicketExportStrategy(ObjectMapper objectMapper, RepairTicketExportExelHandler repairTicketExportExelHandler, RepairTicketExportCsvHandler repairTicketExportCsvHandler) {
        super(objectMapper);
        this.repairTicketExportExelHandler = repairTicketExportExelHandler;
        this.repairTicketExportCsvHandler = repairTicketExportCsvHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.REPAIR_TICKET;
    }

    @Override
    public CustomMultipartFile generateReport(Object filterObject, ContentType contentType) {
        RepairTicketFilter filter = objectMapper.convertValue(filterObject, RepairTicketFilter.class);
        return switch (contentType) {
            case EXCEL -> new CustomMultipartFile(
                    repairTicketExportExelHandler.generate(filter),
                    repairTicketExportExelHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> new CustomMultipartFile(
                    repairTicketExportCsvHandler.generate(filter),
                    repairTicketExportCsvHandler.getFileName(filter),
                    CONTENT_TYPE_FOR_CSV
            );
        };
    }
}
