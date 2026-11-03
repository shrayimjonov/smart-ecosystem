package uz.uzkassa.silen.dto.waybill;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WaybillDocOld implements Serializable {
    @JsonProperty("OldWaybillLocalId")
    private String oldWaybillLocalId;
    @JsonProperty("OldWaybillNo")
    private String oldWaybillNo;
    @JsonProperty("OldWaybillDate")
    private LocalDate oldWaybillDate;

    @JsonSetter("oldwaybilllocalid")
    public void setoldwaybilllocalidLowerCase(String oldWaybillLocalId) {
        this.oldWaybillLocalId = oldWaybillLocalId;
    }

    @JsonSetter("oldwaybillno")
    public void setoldwaybillnoLowerCase(String oldWaybillNo) {
        this.oldWaybillNo = oldWaybillNo;
    }

    @JsonSetter("oldwaybilldate")
    public void setoldwaybilldateLowerCase(LocalDate oldWaybillDate) {
        this.oldWaybillDate = oldWaybillDate;
    }

}
