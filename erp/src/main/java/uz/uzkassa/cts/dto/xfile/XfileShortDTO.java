package uz.uzkassa.cts.dto.xfile;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.XfileStatus;

import java.io.Serial;
import java.io.Serializable;

/**
 * Created date time: 03.03.2025 10:51
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class XfileShortDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1834728744L;

    String id;

    XfileStatus status;
}
