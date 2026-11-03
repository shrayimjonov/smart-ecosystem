package uz.uzkassa.cts.dto.leaverequestreason;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.LocalizedDTO;
import uz.uzkassa.cts.enums.LeaveRequestReasonAbbreviation;
import uz.uzkassa.cts.enums.LeaveRequestReasonType;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LeaveRequestReasonListDTO extends LocalizedDTO {

    LeaveRequestReasonAbbreviation abbreviation;

    String description;

    LeaveRequestReasonType type;

    String color;
}
