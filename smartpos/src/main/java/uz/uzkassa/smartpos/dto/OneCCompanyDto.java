package uz.uzkassa.smartpos.dto;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.company.OneCCompany;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link OneCCompany}
 */
@Getter
@Setter
public class OneCCompanyDto implements Serializable {
    private Long id;
    private String tin;
    private String name;
    private LocalDateTime firstTime = LocalDateTime.now();
    private LocalDateTime lastTime = LocalDateTime.now();
    private Long receiptCount = 0L;
}
