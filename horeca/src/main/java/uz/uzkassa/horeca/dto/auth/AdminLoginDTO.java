package uz.uzkassa.horeca.dto.auth;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class AdminLoginDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3434454351L;

    @Size(min = 1, max = 50)
    String username;

    @Size(min = 4, max = 100)
    String password;

    boolean rememberMe;
}
