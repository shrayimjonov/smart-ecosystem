package uz.uzkassa.cts.domain.regionalassignment;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.domain.address.Region;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.CommonStatus;

import java.io.Serial;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "regional_assignment")
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE regional_assignment SET deleted = 'true' WHERE id=?")
public class RegionalAssignment extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 8347977495922L;

    @Column(name = "employee_id", nullable = false)
    Long employeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", insertable = false, updatable = false)
    User employee;

    @Column(name = "region_id", nullable = false)
    Long regionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", insertable = false, updatable = false)
    Region region;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    CommonStatus status = CommonStatus.ACTIVE;

    @OneToMany(mappedBy = "regionalAssignment", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<RegionalAssignmentDistrict> regionalAssignmentDistricts = new HashSet<>();

    public void addRegionalAssignmentDistrict(RegionalAssignmentDistrict regionalAssignmentDistrict) {
        regionalAssignmentDistrict.setRegionalAssignment(this);
        regionalAssignmentDistrict.setRegionalAssignmentId(this.getId());
        this.regionalAssignmentDistricts.add(regionalAssignmentDistrict);
    }

}
