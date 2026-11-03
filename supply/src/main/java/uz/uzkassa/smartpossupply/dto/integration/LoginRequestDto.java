package uz.uzkassa.smartpossupply.dto.integration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginRequestDto {

    private String username;
    private String password;
}
