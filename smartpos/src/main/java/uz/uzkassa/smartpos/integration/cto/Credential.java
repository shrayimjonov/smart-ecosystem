package uz.uzkassa.smartpos.integration.cto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Credential implements Serializable {
    private String username;
    private String password;
}
