package uz.uzkassa.horeca.dto.companybank;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyBankDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3432222L;

    Long bankId;

    Long companyId;

    String accountNumber;
}
