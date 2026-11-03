package uz.uzkassa.cts.dto.note.cabinet;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.file.FileDTO;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class NoteListCabinetDTO implements Serializable {

    Long id;

    String description;

    LocalDateTime createdDate;

    CommonDTO creator;

    FileDTO file;
}
