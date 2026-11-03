package uz.uzkassa.smartpossupply.dto.integration.agreement;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class AgreementManagerRequestDTO implements Serializable {

    private String tinPinfl;
    private String managerId;
    private String managerLogin;
    private String managerFirstName;
    private String managerLastName;
    private String managerPatronymic;
}
