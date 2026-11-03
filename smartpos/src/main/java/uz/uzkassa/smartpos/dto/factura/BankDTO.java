package uz.uzkassa.smartpos.dto.factura;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BankDTO implements Serializable {
    private String id;
    private String name;
}
