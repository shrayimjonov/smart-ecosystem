package uz.uzkassa.cts.dto.file;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.DocumentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class FileDetailDTO extends CommonDTO implements Serializable {

    String originalName;

    String url;

    String contentType;

    Long size;

    Long entityId;

    EntityTypeEnum entityType;

    DocumentType documentType;
}
