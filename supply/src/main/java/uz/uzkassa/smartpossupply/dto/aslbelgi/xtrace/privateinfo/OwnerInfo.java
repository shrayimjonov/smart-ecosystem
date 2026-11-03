package uz.uzkassa.smartpossupply.dto.aslbelgi.xtrace.privateinfo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OwnerInfo implements Serializable {

    String ownerId;

    String ownerTin;

    Map<String, String> ownerName;

    String ownerBusinessPlaceId;
}
