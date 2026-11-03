package uz.uzkassa.horeca.dto.terminal;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class TerminalReceiptExtraDetailDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 43534534L;

    String packageCode;

    String packageName;
}
