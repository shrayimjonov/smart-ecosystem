package uz.uzkassa.cts.dto.cases.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PhoneNumberChangeDto {
    private String director;
    private String companyName;
    private String tin;
    private String phoneNumber;
    private String newPhoneNumber;
    private LocalDate date;
    private String businessType;
    private Boolean changeInSmartpos;
}
