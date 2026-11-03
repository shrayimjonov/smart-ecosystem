package uz.uzkassa.smartpossupply.dto.integration.agreement;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.smartpossupply.dto.user.FullNameDTO;

@Getter
@Setter
@ToString
public class AgreementManagerDTO extends FullNameDTO {

    protected String login;
    protected String smartposCreatorId;
}
