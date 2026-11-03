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
public class ContractDoc implements Serializable {
    @JsonProperty("ContractNo")
    private String contractNo;
    @JsonProperty("ContractDate")
    private LocalDate contractDate;

    @JsonSetter("contractno")
    public void setcontractnoLowerCase(String contractno) {
        this.contractNo = contractno;
    }

    @JsonSetter("contractdate")
    public void setcontractdateLowerCase(LocalDate contractdate) {
        this.contractDate = contractdate;
    }
}
