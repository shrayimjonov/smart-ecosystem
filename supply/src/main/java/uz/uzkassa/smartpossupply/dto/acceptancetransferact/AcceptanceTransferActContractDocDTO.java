package uz.uzkassa.smartpossupply.dto.acceptancetransferact;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AcceptanceTransferActContractDocDTO implements Serializable {

    @JsonProperty("ContractNo")
    String contractNo;

    @JsonProperty("ContractDate")
    String contractDate;

    @JsonSetter("contractno")
    public void setcontractno(String contractno) {
        this.contractNo = contractno;
    }

    @JsonSetter("contractdate")
    public void setcontractdate(String contractdate) {
        this.contractDate = contractdate;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public void setContractDate(String contractDate) {
        this.contractDate = contractDate;
    }
}
