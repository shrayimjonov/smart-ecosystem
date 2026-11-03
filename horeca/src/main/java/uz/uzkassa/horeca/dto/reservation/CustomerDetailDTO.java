package uz.uzkassa.horeca.dto.reservation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.enums.AgeGroup;
import uz.uzkassa.horeca.enums.Gender;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerDetailDTO extends CommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 9340283L;

    String phone;

    Gender gender;

    AgeGroup ageGroup;

    public CustomerDetailDTO(Long id, String name, String phone) {
        super(id, name);
        this.phone = phone;
    }

    public CustomerDetailDTO(String name, String phone) {
        this(null, name, phone);
    }

    public CustomerDetailDTO(CommonDTO client, String phone) {
        super(client.getId(), client.getName());
        this.phone = phone;
    }
}
