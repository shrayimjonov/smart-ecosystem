package uz.uzkassa.smartpossupply.dto.stock;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.bank.BankDTO;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockCustomerDTO implements Serializable {

    Long id;

    String tin;

    String name;

    BankDTO bank;
}
