package uz.uzkassa.silen.dto.invoice;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class ProviderBindingDTO implements Serializable {
    @JsonProperty("providers")
    private List<ProviderDTO> providers;
}
