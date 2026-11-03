package uz.uzkassa.horeca.dto.post;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.horeca.dto.address.AddressDTO;
import uz.uzkassa.horeca.enums.AttachmentType;

import java.io.Serial;
import java.io.Serializable;


@Getter
@Setter
@NoArgsConstructor
public class PostAttachmentDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 834958L;

    AttachmentType attachmentType;

    String text;

    Long photoId;

    PostPhotoGalleryDTO photoGallery;

    String videoUrl;

    AddressDTO address;
}
