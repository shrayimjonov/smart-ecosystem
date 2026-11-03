package uz.uzkassa.smartpos.dto.integration.agreement;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class ManagerDTO implements Serializable {
    @NotNull
    private List<String> tins;
    @NotNull
    private String managerLogin;
    private String managerFirstName;
    private String managerLastName;
    private String managerPatronymic;
}
