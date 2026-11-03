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
public class WaybillDoc implements Serializable {
    @JsonProperty("WaybillNo")
    private String waybillNo;
    @JsonProperty("WaybillDate")
    private LocalDate waybillDate;

    @JsonSetter("waybillno")
    public void setwaybillnoLowerCase(String waybillNo) {
        this.waybillNo = waybillNo;
    }

    @JsonSetter("waybilldate")
    public void setwaybilldateLowerCase(LocalDate waybilldate) {
        this.waybillDate = waybilldate;
    }

}
