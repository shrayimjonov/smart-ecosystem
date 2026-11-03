package uz.uzkassa.smartpossupply.dto.post;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.address.AddressDetailDTO;
import uz.uzkassa.smartpossupply.dto.file.FileDTO;
import uz.uzkassa.smartpossupply.enums.AttachmentType;

import java.io.Serializable;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 02.11.2023
 */

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostAttachmentDetailDTO implements Serializable {

    AttachmentType attachmentType;

    String text;

    FileDTO photo;

    PostPhotoGalleryDetailDTO photoGallery;

    String videoUrl;

    AddressDetailDTO address;
}
