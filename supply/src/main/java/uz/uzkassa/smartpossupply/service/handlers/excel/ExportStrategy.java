package uz.uzkassa.smartpossupply.service.handlers.excel;

import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.service.CustomMultipartFile;

public interface ExportStrategy {

    EntityTypeEnum getEntityType();

    CustomMultipartFile generateReport(Object filter, FileType fileType);
}
