package uz.uzkassa.cts.dto.cases.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class LegalDataChangeDto {
    private String director;
    private String tin;
    private LocalDate date;
    private String name;
    private String newName;
    private String companyName;
    private String newCompanyName;
    private String businessType;
    private String newBusinessType;
    private String vat;
    private String newVAT;
    private String activityType;
    private String newActivityType;
    private String address;
    private String newAddress;
    private String neighborhoodName;
}
