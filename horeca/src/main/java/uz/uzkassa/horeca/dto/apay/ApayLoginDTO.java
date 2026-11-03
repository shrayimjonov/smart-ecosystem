package uz.uzkassa.horeca.dto.apay;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApayLoginDTO implements Serializable {

    private String login;
    private String password;
}
