package uz.uzkassa.silen.dto.marking;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class PartyMergeDTO implements Serializable {
    private String fromId;
    private String toId;
}
