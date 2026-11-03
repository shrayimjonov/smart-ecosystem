package uz.uzkassa.smartpos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonUserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String patronymic;
    private String phone;
    private String login;
}
