package uz.uzkassa.horeca.dto.terminal;

import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TerminalReceiptStatusDTO implements Serializable {

    private String nameRu;
    private String nameUz;
    private String code;

    public TerminalReceiptStatusDTO(String nameRu, String nameUz, String code) {
        this.nameRu = nameRu;
        this.nameUz = nameUz;
        this.code = code;
    }
}
