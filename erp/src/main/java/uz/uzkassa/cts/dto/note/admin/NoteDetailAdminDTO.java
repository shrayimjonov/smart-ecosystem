package uz.uzkassa.cts.dto.note.admin;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.file.FileDTO;
import uz.uzkassa.cts.enums.EntityTypeEnum;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class NoteDetailAdminDTO implements Serializable {

    Long id;

    String description;

    Long entityId;

    EntityTypeEnum entityType;

    LocalDateTime createdDate;

    CommonDTO creator;

    FileDTO file;
}
