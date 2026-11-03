package uz.uzkassa.smartpos.dto.turon;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CisInfoWrapper<T> {
    private T cisInfo;
}
