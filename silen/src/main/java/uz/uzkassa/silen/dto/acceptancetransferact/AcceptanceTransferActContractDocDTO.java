package uz.uzkassa.silen.dto.acceptancetransferact;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AcceptanceTransferActContractDocDTO implements Serializable {

    @JsonProperty("ContractNo")
    String contractNo;

    @JsonProperty("ContractDate")
    String contractDate;

    @JsonSetter("contractno")
    public void setcontractno(String contractno) {
        this.contractNo = contractno;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    @JsonSetter("contractdate")
    public void setcontractdate(String contractdate) {
        this.contractDate = contractdate;
    }

    public void setContractDate(String contractDate) {
        this.contractDate = contractDate;
    }
}
