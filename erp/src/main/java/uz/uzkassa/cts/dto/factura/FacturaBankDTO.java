package uz.uzkassa.cts.dto.factura;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FacturaBankDTO implements Serializable {
    private String bankId;
    private String name;
}
