package uz.uzkassa.cts.dto.cases.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ManagerChangeDto {
    private String director;
    private String companyName;
    private String tin;
    private String businessType;
    private String managerName;
    private String phoneNumber;
    private String reason;
    private LocalDate date;
}
