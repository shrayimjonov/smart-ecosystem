package uz.uzkassa.smartpos.integration.apay;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpos.domain.postgres.FileUpload;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class FileDTO implements Serializable {
    String id;
    String url;
    String objectName;

    public FileDTO(String id) {
        this.id = id;
    }

    public FileDTO(String id, String url) {
        this.id = id;
        this.url = url;
    }

    public FileDTO(FileUpload fileUpload) {
        this.id = String.valueOf(fileUpload.getId());
        this.url = fileUpload.getUrl();
        this.objectName = fileUpload.getObjectName();
    }
}
