package uz.uzkassa.smartpossupply.dto.post;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.PostPosition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 02.11.2023
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostDetailDTO implements Serializable {

    Long id;

    String title;

    PostPosition position;

    List<PostAttachmentDetailDTO> attachments = new ArrayList<>();

    @JsonIgnore
    public void addAttachment(PostAttachmentDetailDTO postAttachmentDetailDTO) {
        attachments.add(postAttachmentDetailDTO);
    }
}
