package uz.uzkassa.cts.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.EntityTypeEnum;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
public class BaseFilter extends SimpleFilter implements Serializable {

    Long id;

    Long customerId;

    Long customerBranchId;

    Long branchId;

    Long userId;

    Long employeeId;

    Long managerId;

    Long creatorId;

    Long positionId;

    Long departmentId;

    Long deviceId;

    Long modelId;

    Long fiscalModuleId;

    Long companyId;

    Long categoryId;

    String status;

    Long regionId;

    Long districtId;

    Long neighborhoodId;

    Long parentId;

    LocalDate from;

    LocalDate to;

    String tin;

    String type;

    Long entityId;

    EntityTypeEnum entityType;

    public LocalDateTime getFromStartOfDay() {
        return from != null ? from.atStartOfDay() : null;
    }

    public LocalDateTime getToEndOfDay() {
        return to != null ? to.atTime(LocalTime.MAX) : null;
    }

    /*public Long getCompanyId() {
        return Optional.ofNullable(this.companyId).orElseGet(SecurityUtils::getCompanyId);
    }

    public Long getBranchId() {
        return Optional.ofNullable(this.branchId).orElseGet(SecurityUtils::getBranchId);
    }

    public Long getDepartmentId() {
        return Optional.ofNullable(this.departmentId).orElseGet(SecurityUtils::getDepartmentId);
    }*/
}
