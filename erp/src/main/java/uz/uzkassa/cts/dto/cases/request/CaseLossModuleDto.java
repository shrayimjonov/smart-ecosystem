package uz.uzkassa.cts.dto.cases.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CaseLossModuleDto {
    private String companyName;
    private String tin;
    private String fiscalNumber;
    private String deviceNumber;
    private LocalDate date;
    private String director;
}
