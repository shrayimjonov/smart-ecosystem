package uz.uzkassa.horeca.dto.post;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.horeca.enums.PostType;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PostDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4950395L;

    String title;

    LocalDateTime postDate;

    Long branchId;

    PostType postType;

    List<PostAttachmentDTO> attachments;
}
