package uz.uzkassa.smartpos.integration.apay.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpos.enums.ApplicationStatus;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class ApaySubmitApplicationResponseDTO {
    String id;
    String terminalId;
    ApplicationStatus status;
}
