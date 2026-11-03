package uz.uzkassa.smartpossupply.dto.bank;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BankLookUpDTO extends CommonDTO {

    String mfo;

    public BankLookUpDTO(Long id, String name, String mfo) {
        super(id, name);
        this.mfo = mfo;
    }
}
