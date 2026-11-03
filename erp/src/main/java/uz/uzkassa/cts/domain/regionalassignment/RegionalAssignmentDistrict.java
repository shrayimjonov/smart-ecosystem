package uz.uzkassa.cts.domain.regionalassignment;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.domain.address.District;
import uz.uzkassa.cts.domain.base.BaseEntity;

@Getter
@Setter
@Entity
@Table(name = "regional_assignment_district")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegionalAssignmentDistrict extends BaseEntity {

    @Column(name = "regional_assignment_id", nullable = false)
    Long regionalAssignmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "regional_assignment_id", insertable = false, updatable = false)
    RegionalAssignment regionalAssignment;

    @Column(name = "district_id", nullable = false)
    Long districtId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id", insertable = false, updatable = false)
    District district;
}
