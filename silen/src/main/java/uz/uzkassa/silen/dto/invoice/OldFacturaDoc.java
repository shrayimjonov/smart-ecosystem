package uz.uzkassa.silen.dto.invoice;

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
public class OldFacturaDoc implements Serializable {
    @JsonProperty("OldFacturaId")
    private String oldFacturaId;
    @JsonProperty("OldFacturaNo")
    private String oldFacturaNo;
    @JsonProperty("OldFacturaDate")
    private LocalDate oldFacturaDate;

    @JsonSetter("oldfacturaid")
    public void setoldfacturaid(String oldfacturaid) {
        this.oldFacturaId = oldfacturaid;
    }

    public void setOldFacturaId(String oldFacturaId) {
        this.oldFacturaId = oldFacturaId;
    }

    @JsonSetter("oldfacturano")
    public void setoldfacturano(String oldfacturano) {
        this.oldFacturaNo = oldfacturano;
    }

    public void setOldFacturaNo(String oldFacturaNo) {
        this.oldFacturaNo = oldFacturaNo;
    }

    @JsonSetter("oldfacturadate")
    public void setoldfacturadate(LocalDate oldfacturadate) {
        this.oldFacturaDate = oldfacturadate;
    }

    public void setOldFacturaDate(LocalDate oldFacturaDate) {
        this.oldFacturaDate = oldFacturaDate;
    }
}
