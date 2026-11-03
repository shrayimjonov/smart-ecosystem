package uz.uzkassa.silen.dto.invoice;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class LgotaDTO implements Serializable {
    @JsonProperty("newLgotaId")
    private Long lgotaId;
    private String lgotaName;
}
