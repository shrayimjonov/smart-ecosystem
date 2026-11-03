package uz.uzkassa.silen.dto.invoice;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
public class ProviderResultDTO implements Serializable {
    @JsonProperty("ClientTin")
    private String clientTin;
    @JsonProperty("ProviderTins")
    private Set<String> providerTins;
}
