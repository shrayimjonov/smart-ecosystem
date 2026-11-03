package uz.uzkassa.smartpossupply.dto.factura;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacturaForeignCompanyDTO implements Serializable {

    @JsonProperty("CountryId")
    private String countryId;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Address")
    private String address;

    @JsonProperty("Bank")
    private String bank;

    @JsonProperty("Account")
    private String account;

    @JsonSetter("countryid")
    public void setcountryid(String countryid) {
        this.countryId = countryid;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    @JsonSetter("name")
    public void setNameLowerCase(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonSetter("address")
    public void setAddressLowerCase(String address) {
        this.address = address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @JsonSetter("bank")
    public void setBankLowerCase(String bank) {
        this.bank = bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    @JsonSetter("account")
    public void setAccountLowerCase(String account) {
        this.account = account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
