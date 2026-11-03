package uz.uzkassa.smartpossupply.dto.party;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.aggregation.AggregationDTO;
import uz.uzkassa.smartpossupply.enums.PackageType;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PartyProductDTO implements Serializable {

    Long partyId;

    Long productId;

    Long childProductId;

    Set<AggregationDTO> aggregations;

    PackageType packageType;

    Long serialId;
}
