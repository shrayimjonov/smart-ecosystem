package uz.uzkassa.cts.dto.user;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UserShortDTO implements Serializable {
    private Long id;

    private String firstName;

    private String lastName;

    private String login;

    private String patronymic;
}



