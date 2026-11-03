package uz.uzkassa.smartpossupply.dto.factura.supply;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SupplyFacturaUpdateDTO implements Serializable {

    String facturaId;
    String facturaProductId;
    String sellerVatRegCode;
    Integer sellerVatRegStatus;
    String buyerVatRegCode;
    Integer buyerVatRegStatus;
    String agentFacturaId;
}
