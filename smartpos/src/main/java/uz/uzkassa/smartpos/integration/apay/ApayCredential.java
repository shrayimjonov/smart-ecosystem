package uz.uzkassa.smartpos.integration.apay;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApayCredential implements Serializable {
    private String login;
    private String password;
}
