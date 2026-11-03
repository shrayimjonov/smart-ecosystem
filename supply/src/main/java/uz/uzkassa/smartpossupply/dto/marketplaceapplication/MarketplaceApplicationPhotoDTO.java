package uz.uzkassa.smartpossupply.dto.marketplaceapplication;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.file.FileDTO;
import uz.uzkassa.smartpossupply.enums.FileSizeType;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MarketplaceApplicationPhotoDTO extends FileDTO implements Serializable {

    String contentType;

    FileSizeType sizeType;
}
