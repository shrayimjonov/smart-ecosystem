package uz.uzkassa.cts.dto.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OfdNeighborhoodSyncPayload implements Serializable {

    private Long regionId;
    private Long regionCode;

    private Long districtId;
    private Long districtCode;
}
