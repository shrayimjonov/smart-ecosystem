package uz.uzkassa.cts.dto.approvalprocess;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.ApproverStatus;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApproverDetailDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 6674836274512345L;

    Long id;

    CommonDTO employee;

    ApproverStatus status;

    ApproverStatus overallStatus;

    boolean current;

    boolean canAction;

    Integer step;

}
