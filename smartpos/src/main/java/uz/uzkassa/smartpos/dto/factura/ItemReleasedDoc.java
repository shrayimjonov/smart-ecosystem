package uz.uzkassa.smartpos.dto.factura;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ItemReleasedDoc implements Serializable {
    @JsonProperty("ItemReleasedTin")
    private String itemReleasedTin;
    @JsonProperty("ItemReleasedFio")
    private String itemReleasedFio;
    @JsonProperty("ItemReleasedPinfl")
    private String itemReleasedPinfl;
}
