package uz.uzkassa.cts.dto.apay;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.domain.CustomerBank;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class ApayBankDTO implements Serializable {

    String id;

    String name;

    String accountNumber;

    String mfo;

    String oked;

    String tin;

    public ApayBankDTO(CustomerBank customerBank) {
        this.id = customerBank.getId().toString();
        this.accountNumber = customerBank.getAccountNumber();
        if (customerBank.getBank() != null) {
            this.name = customerBank.getBank().getName();
            this.mfo = customerBank.getBank().getCode();
        }
        this.oked = customerBank.getCustomer().getOked();
    }
}
