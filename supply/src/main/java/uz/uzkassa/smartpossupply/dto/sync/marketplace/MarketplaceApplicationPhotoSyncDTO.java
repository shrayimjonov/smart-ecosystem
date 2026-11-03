package uz.uzkassa.smartpossupply.dto.sync.marketplace;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.file.FileDTO;
import uz.uzkassa.smartpossupply.enums.FileSizeType;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MarketplaceApplicationPhotoSyncDTO extends FileDTO implements Serializable {

    String contentType;

    FileSizeType sizeType;

    List<MarketplaceApplicationPhotoSyncDTO> children;
}
