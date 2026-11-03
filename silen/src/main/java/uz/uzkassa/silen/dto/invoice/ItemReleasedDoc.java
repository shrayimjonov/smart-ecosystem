package uz.uzkassa.silen.dto.invoice;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
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

    @JsonSetter("itemreleasedtin")
    public void setitemreleasedtin(String itemreleasedtin) {
        this.itemReleasedTin = itemreleasedtin;
    }

    public void setItemReleasedTin(String itemReleasedTin) {
        this.itemReleasedTin = itemReleasedTin;
    }

    @JsonSetter("itemreleasedfio")
    public void setitemreleasedfio(String itemreleasedfio) {
        this.itemReleasedFio = itemreleasedfio;
    }

    public void setItemReleasedFio(String itemReleasedFio) {
        this.itemReleasedFio = itemReleasedFio;
    }

    @JsonSetter("itemreleasedpinfl")
    public void setitemreleasedpinfl(String itemreleasedpinfl) {
        this.itemReleasedPinfl = itemreleasedpinfl;
    }

    public void setItemReleasedPinfl(String itemReleasedPinfl) {
        this.itemReleasedPinfl = itemReleasedPinfl;
    }
}
