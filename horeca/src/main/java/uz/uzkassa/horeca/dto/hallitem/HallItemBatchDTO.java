package uz.uzkassa.horeca.dto.hallitem;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Setter
@Getter
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor
public class HallItemBatchDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 855533L;

    List<HallItemDTO> items;
}
