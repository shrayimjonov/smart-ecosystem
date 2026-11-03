package uz.uzkassa.cts.dto.sale.act;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
public class SaleActPowerOfAttorneyDTO implements Serializable {
    Long actId;

    Long powerOfAttorneyNumber;

    LocalDate currentDate = LocalDate.now();

    LocalDate dueDate = LocalDate.now();

    String position;

    String fullName;

    String passportSerial;

    String passportNumber;

    String issueBy;

    LocalDate issueDate;

    Integer qty;

    String qtyInWord;
}
