package uz.uzkassa.cts.dto.customerbank;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomBankDTO implements Serializable {

    Long id;

    Long customerId;

    Long bankId;

    String accountNumber;

    boolean isDefault;

    public String getAccountNumber() {
        return accountNumber.replaceAll(" ", "");
    }
}
