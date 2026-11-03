package uz.uzkassa.silen.dto.invoice;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ProviderDTO implements Serializable {
    @JsonProperty("providerTin")
    private String providerTin;
    @JsonProperty("providerName")
    private String providerName;
    @JsonProperty("systemName")
    private String systemName;
    @JsonProperty("enabled")
    private boolean enabled;
}
