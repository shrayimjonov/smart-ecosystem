package uz.uzkassa.smartpossupply.service.handlers.excel;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.service.CustomMultipartFile;
import uz.uzkassa.smartpossupply.service.handlers.excel.export.ExpeditorStockTransferDetailExcelHandler;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExpeditorStockTransferDetailStrategy extends AbstractExportStrategy {

    ExpeditorStockTransferDetailExcelHandler excelHandler;

    public ExpeditorStockTransferDetailStrategy(ExpeditorStockTransferDetailExcelHandler excelHandler) {
        this.excelHandler = excelHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.EXPEDITOR_STOCK_TRANSFER_DETAIL;
    }

    @Override
    public CustomMultipartFile generateReport(Object filter, FileType fileType) {
        BaseFilter baseFilter = objectMapper.convertValue(filter, BaseFilter.class);
        return switch (fileType) {
            case EXCEL -> new CustomMultipartFile(
                    excelHandler.generate(baseFilter),
                    excelHandler.getFileName(baseFilter),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> throw new BadRequestException("CSV not supported");
        };
    }
}
