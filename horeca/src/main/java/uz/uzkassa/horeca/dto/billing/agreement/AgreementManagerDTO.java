package uz.uzkassa.horeca.dto.billing.agreement;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.horeca.dto.base.FullNameDTO;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class AgreementManagerDTO extends FullNameDTO implements Serializable {

    protected String login;
    protected String smartposCreatorId;
}
