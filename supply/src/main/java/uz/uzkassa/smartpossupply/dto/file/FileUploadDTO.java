package uz.uzkassa.smartpossupply.dto.file;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.FileSizeType;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileUploadDTO extends FileDTO {

    String fileId;

    String name;

    String objectName;

    String path;

    Long parentId;

    FileSizeType fileSizeType;
}
