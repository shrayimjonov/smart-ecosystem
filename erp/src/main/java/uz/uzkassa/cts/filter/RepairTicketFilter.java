package uz.uzkassa.cts.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.RepairTicketVisitType;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RepairTicketFilter extends BaseFilter implements Serializable {

    Long typeId;

    Long masterId;

    RepairTicketVisitType visitType;
}
