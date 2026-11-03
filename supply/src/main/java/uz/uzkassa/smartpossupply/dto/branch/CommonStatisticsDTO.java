package uz.uzkassa.smartpossupply.dto.branch;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;


@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommonStatisticsDTO implements Serializable {

    Long activeTotal;

    Long inActiveTotal;

    public CommonStatisticsDTO(Long activeTotal, Long inActiveTotal) {
        this.activeTotal = activeTotal;
        this.inActiveTotal = inActiveTotal;
    }
}
