package uz.uzkassa.horeca.dto.bank;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BankLookUpDTO extends CommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 99378472L;

    String mfo;

    public BankLookUpDTO(Long id, String name, String mfo) {
        super(id, name);
        this.mfo = mfo;
    }
}
