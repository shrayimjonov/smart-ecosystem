package uz.uzkassa.cts.dto.auth;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
public class UsernameDTO implements Serializable {

    String username;

    public UsernameDTO(String username) {
        this.username = username;
    }
}
