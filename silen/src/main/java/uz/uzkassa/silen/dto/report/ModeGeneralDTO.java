package uz.uzkassa.silen.dto.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.silen.enumeration.Mode;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ModeGeneralDTO implements Serializable {
    private String device;
    private String line;
    private Mode mode;
    private Long minutes;
    private Map<String, List<ModeGeneralDTO>> lines;

    public ModeGeneralDTO(String device) {
        this.device = device;
    }

    public ModeGeneralDTO(Mode mode, Long minutes) {
        this.mode = mode;
        this.minutes = minutes;
    }

    public ModeGeneralDTO(String device, String line, String mode, Long minutes) {
        this.device = device;
        this.line = line;
        this.mode = StringUtils.isNotEmpty(mode) ? Mode.valueOf(mode) : null;
        this.minutes = minutes;
    }

    public Map<String, List<ModeGeneralDTO>> getLines() {
        if(lines == null){
            lines = new HashMap<>();
        }
        return lines;
    }
}
