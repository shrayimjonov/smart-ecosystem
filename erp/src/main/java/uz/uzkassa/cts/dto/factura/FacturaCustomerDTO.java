package uz.uzkassa.cts.dto.factura;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class FacturaCustomerDTO implements Serializable {
    @JsonProperty("Name")
    private String name;
    @JsonProperty("Account")
    private String account;
    @JsonProperty("BankId")
    private String bankId;
    @JsonProperty("Address")
    private String address;
    @JsonProperty("Mobile")
    private String mobile;
    @JsonProperty("WorkPhone")
    private String workPhone;
    @JsonProperty("Oked")
    private String oked;
    @JsonProperty("DistrictId")
    private String districtId;
    @JsonProperty("Director")
    private String director;
    @JsonProperty("Accountant")
    private String accountant;
    @JsonProperty("VatRegCode")
    private String vatRegCode;
    @JsonProperty(value = "Category", defaultValue = "6")
    private int category;
}
