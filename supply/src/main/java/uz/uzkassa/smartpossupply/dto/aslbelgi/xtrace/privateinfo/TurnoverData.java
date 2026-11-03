package uz.uzkassa.smartpossupply.dto.aslbelgi.xtrace.privateinfo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TurnoverData implements Serializable {

    OwnerInfo ownerInfo;
}
