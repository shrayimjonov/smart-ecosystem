package uz.uzkassa.cts.dto.customer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CustomerShortDTO extends CommonDTO implements Serializable {

    String tin;

    String phone;

    public CustomerShortDTO(Long id, String name, String tin) {
        super(id, name);
        this.tin = tin;
    }

    public CustomerShortDTO(Long id, String name, String tin, String phone) {
        this(id, name, tin);
        this.phone = phone;
    }
}
