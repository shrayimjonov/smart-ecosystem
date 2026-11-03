package uz.uzkassa.smartpossupply.dto.post;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 02.11.2023
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostPhotoGalleryDTO implements Serializable {

    Integer structure;

    List<Long> photos;
}
