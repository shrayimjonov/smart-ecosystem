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
public class WaybillClientDTO implements Serializable {
    private Long id;
    @JsonProperty("TinOrPinfl")
    private String tinOrPinfl;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("ContractNo")
    private String contractNo;
    @JsonProperty("ContractDate")
    private LocalDate contractDate;
    @JsonProperty("BranchCode")
    private String branchCode;
    @JsonProperty("BranchName")
    private String branchName;
    private Long branchId;

    @JsonSetter("tinorpinfl")
    public void setTinOrPinflToLOwerCase(String tinorpinfl) {
        this.tinOrPinfl = tinorpinfl;
    }

    @JsonSetter("name")
    public void setNameToLOwerCase(String name) {
        this.name = name;
    }

    @JsonSetter("contractno")
    public void setContractNoToLOwerCase(String contractno) {
        this.contractNo = contractno;
    }

    @JsonSetter("contractdate")
    public void setContractDateToLOwerCase(LocalDate contractdate) {
        this.contractDate = contractdate;
    }

    @JsonSetter("branchcode")
    public void setBranchCodeToLOwerCase(String branchcode) {
        this.branchCode = branchcode;
    }

    @JsonSetter("branchname")
    public void setBranchNameToLOwerCase(String branchname) {
        this.branchName = branchname;
    }
}
