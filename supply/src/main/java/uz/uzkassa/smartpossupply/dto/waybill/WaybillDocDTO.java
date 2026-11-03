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
public class WaybillDocDTO implements Serializable {
    @JsonProperty("WaybillNo")
    private String waybillNo;
    @JsonProperty("WaybillDate")
    private LocalDate waybillDate;

    @JsonSetter("waybillno")
    public void setwaybillnoToLowerCase(String waybillNo) {
        this.waybillNo = waybillNo;
    }

    @JsonSetter("waybilldate")
    public void setwaybilldateToLowerCase(LocalDate waybilldate) {
        this.waybillDate = waybilldate;
    }

    public WaybillDocDTO(String waybillNo, LocalDate waybillDate) {
        this.waybillNo = waybillNo;
        this.waybillDate = waybillDate;
    }
}
