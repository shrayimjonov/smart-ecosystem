package uz.uzkassa.cts.dto.auth;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PROTECTED)
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticateDTO implements Serializable {

    String username;

    String password;
}
