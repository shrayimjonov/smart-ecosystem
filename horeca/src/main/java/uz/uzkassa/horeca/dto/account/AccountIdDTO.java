package uz.uzkassa.horeca.dto.account;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountIdDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8783743L;

    Long id;
}
