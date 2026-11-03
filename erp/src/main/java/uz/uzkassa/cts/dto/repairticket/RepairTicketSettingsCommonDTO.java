package uz.uzkassa.cts.dto.repairticket;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
@NoArgsConstructor
public class RepairTicketSettingsCommonDTO extends CommonDTO {

    Long productId;

    public RepairTicketSettingsCommonDTO(Long id, String name, Long productId) {
        super(id, name);
        this.productId = productId;
    }
}
