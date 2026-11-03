package uz.uzkassa.smartpossupply.dto.waybill;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;


@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OldWaybillDocDTO implements Serializable {

    @JsonProperty("OldWaybillLocalId")
    private String oldWaybillLocalId;

    @JsonProperty("OldWaybillNo")
    private String oldWaybillNo;

    @JsonProperty("OldWaybillDate")
    private LocalDate oldWaybillDate;

    @JsonSetter("oldwaybillno")
    public void setwaybillnoToLowerCase(String waybillNo) {
        this.oldWaybillNo = waybillNo;
    }

    @JsonSetter("oldwaybilldate")
    public void setoldwaybilldateToLowerCase(LocalDate waybilldate) {
        this.oldWaybillDate = waybilldate;
    }

    @JsonSetter("oldwaybilllocalid")
    public void setoldwaybilllocalidToLowerCase(String oldWaybillLocalId) {
        this.oldWaybillLocalId = oldWaybillLocalId;
    }

    public OldWaybillDocDTO(String oldWaybillLocalId, String oldWaybillNo, LocalDate oldWaybillDate) {
        this.oldWaybillLocalId = oldWaybillLocalId;
        this.oldWaybillNo = oldWaybillNo;
        this.oldWaybillDate = oldWaybillDate;
    }
}
