package uz.uzkassa.smartpossupply.dto.ofd;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OfdAuthLoginDTO implements Serializable {

    String username;

    String password;
}
