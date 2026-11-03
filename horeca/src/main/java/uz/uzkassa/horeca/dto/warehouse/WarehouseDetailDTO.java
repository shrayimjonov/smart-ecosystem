package uz.uzkassa.horeca.dto.warehouse;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.enums.CommonStatus;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class WarehouseDetailDTO extends CommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 453454L;

    CommonDTO branch;

    CommonDTO manager;

    CommonDTO department;

    CommonStatus status;
}
