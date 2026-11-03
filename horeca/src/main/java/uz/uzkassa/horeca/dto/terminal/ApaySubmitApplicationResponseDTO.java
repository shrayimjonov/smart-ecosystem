package uz.uzkassa.horeca.dto.terminal;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.enums.ApayApplicationStatus;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class ApaySubmitApplicationResponseDTO {
    String id;
    String terminalId;
    ApayApplicationStatus status;
}
