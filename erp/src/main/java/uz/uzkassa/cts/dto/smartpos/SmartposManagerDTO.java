package uz.uzkassa.cts.dto.smartpos;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class SmartposManagerDTO implements Serializable {
    private List<String> tins;
    private String managerLogin;
    private String managerFirstName;
    private String managerLastName;
    private String managerPatronymic;

    @Override
    public String toString() {
        return "SmartposManagerDTO{" +
                "tins=" + tins +
                ", managerLogin='" + managerLogin + '\'' +
                ", managerFirstName='" + managerFirstName + '\'' +
                ", managerLastName='" + managerLastName + '\'' +
                ", managerPatronymic='" + managerPatronymic + '\'' +
                '}';
    }
}
