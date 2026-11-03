package uz.uzkassa.smartpossupply.dto.checklist;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

import java.io.Serializable;

@Getter
@Setter
public class ChecklistResultDTO implements Serializable {

    Long id;

    Boolean checked;

    CommonDTO condition;
}
