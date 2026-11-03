package uz.uzkassa.cts.dto.fm;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SwapDTO implements Serializable {

    Long fiscalizationId;

    String reason;

    String otherReason;
}
