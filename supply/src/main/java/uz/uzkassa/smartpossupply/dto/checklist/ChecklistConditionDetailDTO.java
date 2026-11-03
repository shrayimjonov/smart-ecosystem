package uz.uzkassa.smartpossupply.dto.checklist;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

import java.io.Serializable;

@Getter
@Setter
public class ChecklistConditionDetailDTO extends CommonDTO implements Serializable {

    CommonDTO entity;
}
