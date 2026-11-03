package uz.uzkassa.smartpossupply.dto.factura;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacturaShortDTO implements Serializable {

    private String facturaId;

    private String facturaProductId;

    private String buyerTin;

    private String sellerTin;

    private Integer currentStateId;

    public Status getState() {
        if (getCurrentStateId() == null) {
            return Status.PENDING;
        }
        return switch (getCurrentStateId()) {
            case 17 -> Status.CANCELLED;
            case 20 -> Status.REJECTED;
            case 30 -> Status.ACCEPTED;
            default -> Status.PENDING;
        };
    }
}
