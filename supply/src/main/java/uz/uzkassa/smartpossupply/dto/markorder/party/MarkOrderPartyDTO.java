package uz.uzkassa.smartpossupply.dto.markorder.party;

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
public class MarkOrderPartyDTO implements Serializable {

    Long markOrderProductId;

    Integer qty;
}
