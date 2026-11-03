package uz.uzkassa.horeca.dto.tasnif;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class TasnifResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 34534543L;

    boolean success;

    int code;

    String reason;
}
