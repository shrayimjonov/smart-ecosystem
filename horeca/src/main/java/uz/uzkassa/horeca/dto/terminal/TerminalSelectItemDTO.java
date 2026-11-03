package uz.uzkassa.horeca.dto.terminal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TerminalSelectItemDTO implements Serializable {

    private Long id;
    private String name;
    private String code;
    private boolean selected;

    public TerminalSelectItemDTO(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
