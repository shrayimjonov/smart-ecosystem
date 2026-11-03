package uz.uzkassa.cts.dto.whitelist;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.WhiteListAction;

import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class WhiteListDetailDTO {

    Long id;

    CommonDTO customer;

    LocalDate fromDate;

    LocalDate toDate;

    WhiteListAction action;

    String description;
}
