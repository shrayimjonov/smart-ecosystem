package uz.uzkassa.cts.dto.factura;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
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
}
