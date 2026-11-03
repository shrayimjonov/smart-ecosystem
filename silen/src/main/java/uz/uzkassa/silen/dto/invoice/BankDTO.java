package uz.uzkassa.silen.dto.invoice;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BankDTO implements Serializable {
    private String bankId;
    private String name;
}
