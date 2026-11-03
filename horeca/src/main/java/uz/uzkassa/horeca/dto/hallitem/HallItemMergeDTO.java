package uz.uzkassa.horeca.dto.hallitem;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.UpdateStatusDTO;

import static lombok.AccessLevel.PRIVATE;

@Setter
@Getter
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor
public class HallItemMergeDTO extends UpdateStatusDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 885454454L;

    Long activeId;

    List<Long> blockingIds;

    LocalDateTime startDate;
}
