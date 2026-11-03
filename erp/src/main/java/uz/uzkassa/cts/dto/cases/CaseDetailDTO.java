package uz.uzkassa.cts.dto.cases;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.customer.CustomerLookUpDTO;
import uz.uzkassa.cts.enums.CaseStatus;
import uz.uzkassa.cts.enums.CaseType;
import uz.uzkassa.cts.enums.Priority;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CaseDetailDTO implements Serializable {

    private Long id;
    private CaseStatus status;
    private Priority priority;
    private CaseType caseType;
    private CommonDTO operator;
    private CommonDTO assignee;
    private CustomerLookUpDTO customer;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private Object data;
}
