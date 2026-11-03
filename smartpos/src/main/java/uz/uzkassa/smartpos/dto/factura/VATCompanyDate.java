package uz.uzkassa.smartpos.dto.factura;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class VATCompanyDate implements Serializable {
    boolean success;
    String reason;
    CompanyVat data;
}
