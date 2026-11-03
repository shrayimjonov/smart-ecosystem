package uz.uzkassa.cts.dto.productphoto.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.file.FileDTO;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductPhotoListAdminDTO implements Serializable {

    Long id;

    FileDTO photo;

    boolean isMain;
}
