package uz.uzkassa.cts.dto.cases.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CtsChangeDto {
    private String director;
    private String companyName;
    private String tin;
    private String customerBranchName;
    private String customerBranchAddress;
    private String fiscalModuleSerialNumber;
    private String deviceSerialNumber;
    private LocalDate date;
}
