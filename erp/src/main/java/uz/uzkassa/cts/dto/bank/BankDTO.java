package uz.uzkassa.cts.dto.bank;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.LocalizedDTO;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class BankDTO extends LocalizedDTO implements Serializable {

    String code;
}
