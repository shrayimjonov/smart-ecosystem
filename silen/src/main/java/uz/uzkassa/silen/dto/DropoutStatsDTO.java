package uz.uzkassa.silen.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DropoutStatsDTO {

    Long boxCount;

    Long blockCount;

    Long bottleCount;

    public DropoutStatsDTO(Long boxCount, Long blockCount, Long bottleCount) {
        this.boxCount = boxCount;
        this.blockCount = blockCount;
        this.bottleCount = bottleCount;
    }
}
