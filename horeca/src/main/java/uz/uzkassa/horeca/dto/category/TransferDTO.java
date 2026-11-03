package uz.uzkassa.horeca.dto.category;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 07.05.2024
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class TransferDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 668858498475L;

    List<Long> toBranchIds;

    Long fromBranchId;
}
