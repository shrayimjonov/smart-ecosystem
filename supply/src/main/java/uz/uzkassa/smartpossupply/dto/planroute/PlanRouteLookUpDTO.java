package uz.uzkassa.smartpossupply.dto.planroute;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Date 20.01.2023 10:55
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanRouteLookUpDTO implements Serializable {

    Long id;

    String planRouteNumber;

    LocalDate fromDate;

    LocalDate toDate;

    List<CommonDTO> expeditors = new ArrayList<>();
}
