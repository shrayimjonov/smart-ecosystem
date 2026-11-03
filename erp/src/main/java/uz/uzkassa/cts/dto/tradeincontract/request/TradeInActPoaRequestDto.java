package uz.uzkassa.cts.dto.tradeincontract.request;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class TradeInActPoaRequestDto implements Serializable {
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
