package uz.uzkassa.cts.dto.note;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.EntityTypeEnum;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class NoteDTO implements Serializable {

    Long id;

    String description;

    Long entityId;

    EntityTypeEnum entityType;
}
