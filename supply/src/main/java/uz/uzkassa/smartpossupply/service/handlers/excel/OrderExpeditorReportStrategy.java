package uz.uzkassa.smartpossupply.service.handlers.excel;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.filters.OrderFilter;
import uz.uzkassa.smartpossupply.service.CustomMultipartFile;
import uz.uzkassa.smartpossupply.service.handlers.excel.export.OrderExpeditorReportExcelHandler;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class OrderExpeditorReportStrategy extends AbstractExportStrategy {

    OrderExpeditorReportExcelHandler excelHandler;

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.ORDER_EXPEDITOR_REPORT;
    }

    @Override
    public CustomMultipartFile generateReport(Object filter, FileType fileType) {
        OrderFilter orderFilter = objectMapper.convertValue(filter, OrderFilter.class);
        return switch (fileType) {
            case CSV -> throw new BadRequestException("CSV пока поддерживается");
            case EXCEL -> new CustomMultipartFile(
                    excelHandler.generate(orderFilter),
                    excelHandler.getFileName(orderFilter),
                    CONTENT_TYPE_FOR_EXCEL
            );
        };
    }
}
