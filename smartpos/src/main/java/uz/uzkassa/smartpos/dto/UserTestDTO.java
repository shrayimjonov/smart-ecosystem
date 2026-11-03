package uz.uzkassa.smartpos.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserTestDTO {
    private String login;
    private String password;
    private String firstName;
    private String lastName;
    private String companyName;
}
