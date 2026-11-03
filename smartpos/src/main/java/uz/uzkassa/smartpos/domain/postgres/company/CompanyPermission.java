package uz.uzkassa.smartpos.domain.postgres.company;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.integration.billing.enums.XizmatType;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "company_permission")
public class CompanyPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String serviceType;
    @Enumerated(EnumType.STRING)
    private XizmatType xizmatType = XizmatType.GROUPED;
    @Column(name = "company_id")
    private Long companyId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", insertable = false, updatable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Company company;
    private LocalDateTime endDate;
}
