package uz.uzkassa.smartpos.dto.warehouse;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for {@link uz.uzkassa.smartpos.domain.postgres.warehouse.MarkingIncomeDetail}
 */
@Getter
@Setter
public class MarkingIncomeDetailDto implements Serializable {
    private Long id;
    private String name;
    private List<IncomeMarkDto> markings = new ArrayList<>();
}