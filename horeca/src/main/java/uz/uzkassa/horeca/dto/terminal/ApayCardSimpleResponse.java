package uz.uzkassa.horeca.dto.terminal;

import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApayCardSimpleResponse implements Serializable {
    String id;
    String number;
    String name;
    String cardBackground;
    String cardMiniBackground;
    String bankLogo;
}
