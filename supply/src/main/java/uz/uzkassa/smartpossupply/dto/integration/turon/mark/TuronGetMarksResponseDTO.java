package uz.uzkassa.smartpossupply.dto.integration.turon.mark;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
public class TuronGetMarksResponseDTO implements Serializable {

    private Set<String> codes;
    private String blockId;
}
