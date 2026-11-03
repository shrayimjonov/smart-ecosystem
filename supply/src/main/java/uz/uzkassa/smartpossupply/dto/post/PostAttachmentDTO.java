package uz.uzkassa.smartpossupply.dto.post;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpossupply.enums.AttachmentType;

import java.io.Serializable;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 02.11.2023
 */

@Getter
@Setter
@NoArgsConstructor
public class PostAttachmentDTO implements Serializable {

    AttachmentType attachmentType;

    String text;

    Long photoId;

    PostPhotoGalleryDTO photoGallery;
}
