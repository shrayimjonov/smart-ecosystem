package uz.uzkassa.smartpos.dto.factura;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class LgotaDTO implements Serializable {
    private Long lgotaId;
    private String lgotaName;
}
