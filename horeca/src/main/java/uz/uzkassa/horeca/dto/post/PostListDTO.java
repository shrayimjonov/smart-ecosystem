package uz.uzkassa.horeca.dto.post;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostListDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 84598345L;

    Long id;

    String title;

    CommonDTO branch;

    LocalDateTime postDate;
}
