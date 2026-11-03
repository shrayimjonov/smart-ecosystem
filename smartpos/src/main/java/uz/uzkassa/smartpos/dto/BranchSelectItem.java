package uz.uzkassa.smartpos.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class BranchSelectItem implements Serializable {
    private Long id;
    private String name;
    private String longitude;
    private String latitude;
    private Long regionCode;
    private Long districtCode;

}
