package uz.uzkassa.horeca.dto.post;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostDetailDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 340950349L;

    Long id;

    String title;

    CommonDTO branch;

    List<PostAttachmentDetailDTO> attachments = new ArrayList<>();

    @JsonIgnore
    public void addAttachment(PostAttachmentDetailDTO postAttachmentDetailDTO) {
        attachments.add(postAttachmentDetailDTO);
    }
}
