package uz.uzkassa.horeca.dto.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.horeca.dto.file.FileDTO;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostPhotoGalleryDetailDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 348594385L;

    Integer structure;

    List<FileDTO> photos;
}
