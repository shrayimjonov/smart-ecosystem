package uz.uzkassa.cts.dto.factura;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class FacturaResponseDto implements Serializable {
    private String status;
    private String code;
    private String message;
    private String data;
}
