package uz.uzkassa.smartpossupply.dto.aslbelgi.aic.xtrace;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class AicDisband implements Serializable {

    /**
     * Дата и время выполнения операции
     * в соответствии с ISO 8601
     */
    @NotNull
    LocalDateTime businessDatetime;

    /**
     * АИК, подлежащий расформированию
     */
    @NotNull
    String code;
}
