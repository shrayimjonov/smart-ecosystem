package uz.uzkassa.cts.dto.regionalassignment;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.CommonStatus;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegionalAssignmentDetailDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    Long id;

    CommonDTO region;

    CommonStatus status;

    List<CommonDTO> districts = new ArrayList<>();

    CommonDTO creator;

    LocalDateTime createdDate;

    CommonDTO employee;

    public void addDistrict(CommonDTO district) {
        this.districts.add(district);
    }
}
