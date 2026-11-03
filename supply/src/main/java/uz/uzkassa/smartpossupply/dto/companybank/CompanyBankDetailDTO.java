package uz.uzkassa.smartpossupply.dto.companybank;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.bank.BankLookUpDTO;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CompanyBankDetailDTO implements Serializable {

    Long id;

    BankLookUpDTO bank;

    BankLookUpDTO parentBank;

    String accountNumber;

    String oked;

    Boolean main;
}
