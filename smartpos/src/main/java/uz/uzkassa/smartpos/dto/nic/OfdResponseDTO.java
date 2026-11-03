package uz.uzkassa.smartpos.dto.nic;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.smartpos.integration.ofd.OfdErrorDTO;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class OfdResponseDTO {
    private boolean success;
    private Integer code;
    private String text;
    private String reason;
    private List<OfdErrorDTO> errors;
}
