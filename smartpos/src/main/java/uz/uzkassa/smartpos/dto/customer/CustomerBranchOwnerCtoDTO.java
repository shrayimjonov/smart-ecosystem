package uz.uzkassa.smartpos.dto.customer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpos.domain.postgres.user.User;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CustomerBranchOwnerCtoDTO implements Serializable {
    Long id;
    String firstName;
    String lastName;
    String patronymic;
    String phone;

    public CustomerBranchOwnerCtoDTO(User owner) {
        this.id = owner.getId();
        this.firstName = owner.getFirstName();
        this.lastName = owner.getLastName();
        this.patronymic = owner.getPatronymic();
        this.phone = owner.getLogin();
    }
}
