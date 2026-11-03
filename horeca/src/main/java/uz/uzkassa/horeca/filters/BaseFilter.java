package uz.uzkassa.horeca.filters;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.utils.HeaderUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
public class BaseFilter extends CoreFilter implements Serializable {

    Long id;

    Long userId;

    Long parentId;

    Long companyId;

    Long branchId;

    Long departmentId;

    Long warehouseId;

    Long regionId;

    Long districtId;

    Long categoryId;

    String status;

    List<String> statuses;

    String tin;

    String type;

    List<String> types;

    Long productId;

    Long unitId;

    Long hallId;

    public Long getCompanyId() {
        return Optional.ofNullable(this.companyId).orElse(HeaderUtils.getCurrentCompanyId());
    }

    public Long getBranchId() {
        return Optional.ofNullable(this.branchId).orElse(HeaderUtils.getCurrentBranchId());
    }

    public Long getDepartmentId() {
        return Optional.ofNullable(this.departmentId).orElse(HeaderUtils.getCurrentDepartmentId());
    }
}
