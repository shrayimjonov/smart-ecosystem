package uz.uzkassa.cts.dto.factura;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FacturaItemReleasedDocDTO implements Serializable {
    @JsonProperty("ItemReleasedTin")
    private String itemReleasedTin;
    @JsonProperty("ItemReleasedFio")
    private String itemReleasedFio;
    @JsonProperty("ItemReleasedPinfl")
    private String itemReleasedPinfl;
}
