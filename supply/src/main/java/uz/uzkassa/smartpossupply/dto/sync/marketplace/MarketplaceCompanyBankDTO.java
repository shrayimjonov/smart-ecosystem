package uz.uzkassa.smartpossupply.dto.sync.marketplace;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.bank.BankLookUpDTO;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MarketplaceCompanyBankDTO implements Serializable {

    Long id;

    String accountNumber;

    BankLookUpDTO bank;
}
