package uz.uzkassa.smartpossupply.dto.acceptancetransferact;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AcceptanceTransferActBuyerDTO extends AcceptanceTransferActSellerDTO {

    @JsonProperty("VatRegCode")
    String vatRegCode;

    @JsonProperty("VatRegStatus")
    Integer vatRegStatus;

    @JsonProperty("BranchCode")
    String branchCode;

    @JsonProperty("BranchName")
    String branchName;

    @JsonSetter("vatregcode")
    public void setvatregcode(String vatregcode) {
        this.vatRegCode = vatregcode;
    }

    @JsonSetter("vatregstatus")
    public void setvatregstatus(Integer vatregstatus) {
        this.vatRegStatus = vatregstatus;
    }

    @JsonSetter("branchcode")
    public void setbranchcode(String branchcode) {
        this.branchCode = branchcode;
    }

    @JsonSetter("branchname")
    public void setbranchname(String branchname) {
        this.branchName = branchname;
    }

    public void setVatRegCode(String vatRegCode) {
        this.vatRegCode = vatRegCode;
    }

    public void setVatRegStatus(Integer vatRegStatus) {
        this.vatRegStatus = vatRegStatus;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }
}
