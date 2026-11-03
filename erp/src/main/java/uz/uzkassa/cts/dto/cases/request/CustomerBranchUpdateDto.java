package uz.uzkassa.cts.dto.cases.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CustomerBranchUpdateDto {
    private String director;
    private String companyName;
    private String tin;
    private LocalDate date;
    private String branchName;
    private String newBranchName;
    private String deviceNumber;
    private String activityType;
    private String newActivityType;
    private String businessType;
}
