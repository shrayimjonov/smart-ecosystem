package uz.uzkassa.smartpos.dto.eimzo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class TimeStampInfoDto implements Serializable {
    private String time;
}
