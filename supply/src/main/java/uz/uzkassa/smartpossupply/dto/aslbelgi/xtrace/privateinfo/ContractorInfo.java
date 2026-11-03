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
public class ContractorInfo implements Serializable {

    String contractorId;

    String contractorTin;

    Map<String, String> contractorName;

    String contractorBusinessPlaceId;
}
