package uz.uzkassa.smartpos.integration.ofd;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.nic.OfdResponseDTO;

@Getter
@Setter
public class OfdFullResponseDTO extends OfdResponseDTO {

    private Integer recordTotal;

    public OfdFullResponseDTO() {
    }
}
