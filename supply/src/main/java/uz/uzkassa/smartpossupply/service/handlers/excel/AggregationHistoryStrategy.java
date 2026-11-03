package uz.uzkassa.smartpossupply.service.handlers.excel;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.aggregation.AggregationHistoryExportDTO;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.service.CustomMultipartFile;
import uz.uzkassa.smartpossupply.service.handlers.excel.export.AggregationHistoryExportExcelHandler;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;

import java.util.List;


@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AggregationHistoryStrategy extends AbstractExportStrategy {

    AggregationHistoryExportExcelHandler excelHandler;

    public AggregationHistoryStrategy(AggregationHistoryExportExcelHandler excelHandler) {
        this.excelHandler = excelHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.AGGREGATION_HISTORY;
    }

    @Override
    public CustomMultipartFile generateReport(Object filter, FileType fileType) {
        List<AggregationHistoryExportDTO> items = objectMapper.convertValue(filter, new TypeReference<List<AggregationHistoryExportDTO>>() {
        });
        return switch (fileType) {
            case EXCEL -> new CustomMultipartFile(
                    excelHandler.generate(items),
                    excelHandler.getFileName(),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> throw new BadRequestException("Aggregation history can only be exported to excel");
        };
    }
}
