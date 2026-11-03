package uz.uzkassa.horeca.dto.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostPhotoGalleryDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 34593485L;

    Integer structure;

    List<Long> photos;
}
