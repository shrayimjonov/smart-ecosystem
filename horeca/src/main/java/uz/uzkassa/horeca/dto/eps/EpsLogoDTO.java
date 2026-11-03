package uz.uzkassa.horeca.dto.eps;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.domain.FileUpload;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class EpsLogoDTO implements Serializable {

    String id;
    String url;
    String objectName;

    public EpsLogoDTO(String id) {
        this.id = id;
    }

    public EpsLogoDTO(String id, String url) {
        this.id = id;
        this.url = url;
    }

    public EpsLogoDTO(FileUpload fileUpload) {
        this.url = fileUpload.getUrl();
        this.objectName = fileUpload.getFileName();
    }
}
