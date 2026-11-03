package uz.uzkassa.smartpos.dto.factura;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FacturaCustomer implements Serializable {
    private String tin;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("Account")
    private String account;
    @JsonProperty("BankId")
    private String bankId;
    private String bankName;
    @JsonProperty("Address")
    private String address;
    @JsonProperty("Mobile")
    private String mobile;
    @JsonProperty("WorkPhone")
    private String workPhone;
    @JsonProperty("Oked")
    private String oked;
    private Long regionId;
    private String regionName;
    @JsonProperty("DistrictId")
    private String districtId;
    private String districtName;
    @JsonProperty("Director")
    private String director;
    @JsonProperty("Accountant")
    private String accountant;
    @JsonProperty("VatRegCode")
    private String vatRegCode;
    @JsonProperty("VatRegStatus")
    private Integer vatRegStatus;
    @JsonProperty("BranchCode")
    private String branchCode;
    @JsonProperty("BranchName")
    private String branchName;
    @JsonProperty("TaxGap")
    private BigDecimal taxGap;
}
