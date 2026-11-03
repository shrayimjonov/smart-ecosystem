package uz.uzkassa.smartpossupply.dto.post;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.PostPosition;
import uz.uzkassa.smartpossupply.enums.PostType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 17.10.2022 15:42
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostDTO {

    String title;

    LocalDateTime postDate;

    PostType postType;

    List<PostAttachmentDTO> attachments;

    PostPosition position;

    Long companyId;
}
