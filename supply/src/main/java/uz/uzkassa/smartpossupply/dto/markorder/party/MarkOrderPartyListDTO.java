package uz.uzkassa.smartpossupply.dto.markorder.party;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.MarkStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MarkOrderPartyListDTO implements Serializable {

    Long id;

    CommonDTO markOrderProduct;

    Integer qty;

    LocalDateTime createdDate;

    String reportId;

    LocalDateTime reportTime;

    MarkStatus status;
}
