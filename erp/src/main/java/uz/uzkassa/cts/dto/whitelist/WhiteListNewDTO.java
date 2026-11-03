package uz.uzkassa.cts.dto.whitelist;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.WhiteListAction;

import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class WhiteListNewDTO {

    Long id;

    Long customerId;

    LocalDate fromDate;

    LocalDate toDate;

    WhiteListAction action;

    CommonStatus status;

    String description;
}
