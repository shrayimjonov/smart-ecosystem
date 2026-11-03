package uz.uzkassa.cts.dto.customer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CustomerBranchResponseAPayDTO implements Serializable {

    Long branchId;
    String branchName;
    boolean deleted;
    LocalDateTime createdDate;
    LocalDateTime updatedDate;
    String regionCode;
    String districtCode;
    String longitude;
    String latitude;
    String categoryCode;
    String parentCategoryCode;
    String companyName;
}
