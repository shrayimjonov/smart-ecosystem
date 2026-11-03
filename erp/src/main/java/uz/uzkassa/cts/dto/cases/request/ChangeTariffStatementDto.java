package uz.uzkassa.cts.dto.cases.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ChangeTariffStatementDto {
    private String director;
    private String companyName;
    private String tin;
    private LocalDate date;
    private String tariffName;
    private String newTariffName;
    private String deviceSerialNumbers;
    private String businessType;
}
