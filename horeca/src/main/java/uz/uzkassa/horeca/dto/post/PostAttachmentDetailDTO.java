package uz.uzkassa.horeca.dto.post;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.horeca.dto.address.AddressDetailDTO;
import uz.uzkassa.horeca.dto.file.FileDTO;
import uz.uzkassa.horeca.enums.AttachmentType;

import java.io.Serial;
import java.io.Serializable;


@Getter
@Setter
@NoArgsConstructor
public class PostAttachmentDetailDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 85943859L;

    AttachmentType attachmentType;

    String text;

    FileDTO photo;

    PostPhotoGalleryDetailDTO photoGallery;

    String videoUrl;

    AddressDetailDTO address;
}
