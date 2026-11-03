package uz.uzkassa.horeca.dto.companybank;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.bank.BankLookUpDTO;

import java.io.Serial;
import java.io.Serializable;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CompanyBankDetailDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 88938233L;

    Long id;

    BankLookUpDTO bank;

    String accountNumber;

    String oked;
}
