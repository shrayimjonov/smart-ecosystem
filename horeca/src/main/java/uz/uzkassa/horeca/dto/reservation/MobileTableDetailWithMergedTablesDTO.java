package uz.uzkassa.horeca.dto.reservation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.enums.HallItemStatus;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 27.03.2024
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MobileTableDetailWithMergedTablesDTO extends CommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8569834L;

    Long numberOfGuest;

    CommonDTO hall;

    List<CommonDTO> mergedTables;

    HallItemStatus status;

    public void addTable(CommonDTO table, Long numberOfGuest) {
        if (mergedTables == null) {
            mergedTables = new ArrayList<>();
        }
        mergedTables.add(table);
        this.numberOfGuest += numberOfGuest;
    }

}
