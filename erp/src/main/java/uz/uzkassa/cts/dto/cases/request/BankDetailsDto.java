package uz.uzkassa.cts.dto.cases.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BankDetailsDto {
    private String director;
    private String companyName;
    private String tin;
    private String bankName;
    private String accountNumber;
    private LocalDate date;
    private String mfo;
    private String oked;
    private String businessType;
}
