package uz.uzkassa.smartpossupply.dto.auth;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CabinetLoginDTO extends PhoneDTO {

    @Serial
    private static final long serialVersionUID = 1092426715809712337L;

    @NotNull
    @Size(min = 6, max = 100)
    String password;

    boolean rememberMe;
}
