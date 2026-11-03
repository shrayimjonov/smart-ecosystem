package uz.uzkassa.smartpos.integration.apay.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ApayBankLogoDTO implements Serializable {
    private String logo;
    private String background;
}
