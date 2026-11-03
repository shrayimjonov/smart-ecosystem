package uz.uzkassa.smartpossupply.dto.factura;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class FacturaCompanyDTO implements Serializable {

    private String id;
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

    @JsonSetter("name")
    public void setNameLowerCase(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonSetter("account")
    public void setAccountLowerCase(String account) {
        this.account = account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @JsonSetter("bankid")
    public void setbankid(String bankid) {
        this.bankId = bankid;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    @JsonSetter("address")
    public void setAddressLowerCase(String address) {
        this.address = address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @JsonSetter("mobile")
    public void setMobileLowerCase(String mobile) {
        this.mobile = mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @JsonSetter("workphone")
    public void setworkphone(String workphone) {
        this.workPhone = workphone;
    }

    public void setWorkPhone(String workPhone) {
        this.workPhone = workPhone;
    }

    @JsonSetter("oked")
    public void setOkedLowerCase(String oked) {
        this.oked = oked;
    }

    public void setOked(String oked) {
        this.oked = oked;
    }

    @JsonSetter("districtid")
    public void setdistrictid(String districtid) {
        this.districtId = districtid;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId;
    }

    @JsonSetter("director")
    public void setDirectorLowerCase(String director) {
        this.director = director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    @JsonSetter("accountant")
    public void setAccountantLowerCase(String accountant) {
        this.accountant = accountant;
    }

    public void setAccountant(String accountant) {
        this.accountant = accountant;
    }

    @JsonSetter("vatregcode")
    public void setvatregcode(String vatregcode) {
        this.vatRegCode = vatregcode;
    }

    public void setVatRegCode(String vatRegCode) {
        this.vatRegCode = vatRegCode;
    }

    @JsonSetter("vatregstatus")
    public void setvatregstatus(Integer vatregstatus) {
        this.vatRegStatus = vatregstatus;
    }

    public void setVatRegStatus(Integer vatRegStatus) {
        this.vatRegStatus = vatRegStatus;
    }

    @JsonSetter("branchcode")
    public void setbranchcode(String branchcode) {
        this.branchCode = branchcode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    @JsonSetter("branchname")
    public void setbranchname(String branchname) {
        this.branchName = branchname;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }
}
