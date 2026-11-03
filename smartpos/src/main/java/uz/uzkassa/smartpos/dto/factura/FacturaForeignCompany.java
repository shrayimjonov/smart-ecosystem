package uz.uzkassa.smartpos.dto.factura;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class FacturaForeignCompany implements Serializable {
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
}
