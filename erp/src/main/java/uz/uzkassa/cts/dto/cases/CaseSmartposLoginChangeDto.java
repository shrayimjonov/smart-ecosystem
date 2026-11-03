package uz.uzkassa.cts.dto.cases;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CaseSmartposLoginChangeDto {
    private String currentLogin;
    private String newLogin;

    public CaseSmartposLoginChangeDto(String currentLogin, String newLogin) {
        this.currentLogin = currentLogin;
        this.newLogin = newLogin;
    }
}
