package uz.uzkassa.smartpossupply.dto.note;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NoteListDTO extends NoteDTO {

    String createdBy;

    LocalDateTime createdDate;

    public NoteListDTO(Long id, String comment, String createdBy, LocalDateTime createdDate) {
        this.id = id;
        this.comment = comment;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
    }
}
