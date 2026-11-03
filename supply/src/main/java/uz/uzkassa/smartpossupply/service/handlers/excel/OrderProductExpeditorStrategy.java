package uz.uzkassa.smartpossupply.service.handlers.excel;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.order.ProductExpeditorReportCreateDTO;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.service.CustomMultipartFile;
import uz.uzkassa.smartpossupply.service.handlers.excel.export.OrderReportByProductExpeditorExcelHandler;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderProductExpeditorStrategy extends AbstractExportStrategy {

    OrderReportByProductExpeditorExcelHandler excelHandler;

    public OrderProductExpeditorStrategy(OrderReportByProductExpeditorExcelHandler excelHandler) {
        this.excelHandler = excelHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.ORDER_PRODUCT_EXPEDITOR_REPORT;
    }

    @Override
    public CustomMultipartFile generateReport(Object filter, FileType fileType) {
        ProductExpeditorReportCreateDTO productExpeditorReportCreateDTO = objectMapper.convertValue(filter, ProductExpeditorReportCreateDTO.class);
        return switch (fileType) {
            case EXCEL -> new CustomMultipartFile(
                    excelHandler.generate(productExpeditorReportCreateDTO),
                    excelHandler.getFileName(productExpeditorReportCreateDTO),
                    CONTENT_TYPE_FOR_EXCEL
            );

            case CSV -> throw new BadRequestException("CSV не поддерживается");
        };
    }
}
