package uz.uzkassa.cts.dto.contractforleasing.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class GeneratePoaTemplateDTO implements Serializable {

    Long actOfTransmissionId;

    Long powerOfAttorneyNumber;

    LocalDate currentDate = LocalDate.now();

    LocalDate dueDate = LocalDate.now().plusYears(1);

    String position;

    String fullName;

    String passportSerial;

    String passportNumber;

    String issueBy;

    LocalDate issueDate;

    Integer qty;

    String qtyInWord;
}
