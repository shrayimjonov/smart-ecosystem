package uz.uzkassa.horeca.dto.factura;

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
            return null;
        }
        switch (getCurrentStateId()) {
            case 17:
                return Status.CANCELLED;
            case 20:
                return Status.REJECTED;
            case 30:
                return Status.ACCEPTED;
            case 10:
            case 15:
            default:
                return Status.PENDING;
        }
    }
}
