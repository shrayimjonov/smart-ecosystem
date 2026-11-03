package uz.uzkassa.cts.dto.approvalprocess;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApproverDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4460922385674L;

    Long stepId;

    Long employeeId;
}
