package uz.uzkassa.silen.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.silen.enumeration.StoreStatus;

import java.io.Serializable;

/**
 * A DTO for the {@link uz.uzkassa.silen.domain.Store} entity.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class StoreDTO implements Serializable {
    private Long id;
    private String name;
    private String branchCode;
    private StoreStatus status = StoreStatus.ACTIVE;
    private String statusName;
    private String organizationId;
    private String organizationName;
    private Long regionId;
    private String regionName;
    private String districtId;
    private String districtName;
    private String address;
    private String personal;
    private SelectItem personalUser;
    private boolean main;

}
