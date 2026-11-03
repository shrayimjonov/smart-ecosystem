package uz.uzkassa.smartpos.integration.cto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class CtoFiscalCheckDTO implements Serializable {
    private String deviceSerial;
    private String fiscalModuleSerial;
    @NotBlank
    private String hash;
}
