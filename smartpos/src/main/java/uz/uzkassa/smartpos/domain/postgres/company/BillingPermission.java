package uz.uzkassa.smartpos.domain.postgres.company;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.base.audit.AbstractAuditingEntity;
import uz.uzkassa.smartpos.dto.BillingPermissionRequestDto;
import uz.uzkassa.smartpos.integration.billing.enums.XizmatType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@NoArgsConstructor
@Entity
@Table(name = "billing_permission")
@Getter
@Setter
public class BillingPermission extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "company_id")
    private Long companyId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    private Company company;
    @Column(name = "unit_count")
    Integer unitCount;
    BigDecimal amount;
    @Column(name = "start_date")
    LocalDateTime startDate;
    @Column(name = "end_date")
    LocalDateTime endDate;
    @Column(name = "service_type")
    String serviceType;
    String tin;
    String pinfl;
    @Column(name = "bonus_amount")
    BigDecimal bonusAmount;
    @Column(name = "name_ru")
    String nameRu;
    @Column(name = "name_uz_cyrillic")
    String nameUzCyrillic;
    @Column(name = "name_uz_latin")
    String nameUzLatin;
    @Enumerated(EnumType.STRING)
    XizmatType xizmatType;

    public BillingPermission(Long companyId, BillingPermissionRequestDto dto) {
        this.companyId = companyId;
        this.unitCount = dto.getUnitCount();
        this.amount = dto.getAmount();
        this.startDate = Optional.ofNullable(dto.getStartDate()).orElseGet(LocalDateTime::now);
        this.endDate = Optional.ofNullable(dto.getEndDate()).orElseGet(LocalDateTime::now);
        this.serviceType = dto.getServiceType();
        this.tin = dto.getTin();
        this.pinfl = dto.getPinfl();
        this.bonusAmount = dto.getBonusAmount();
        if (dto.getCategoryDTO() != null) {
            this.nameRu = dto.getCategoryDTO().getNameRu();
            this.nameUzCyrillic = dto.getCategoryDTO().getNameUzCyrillic();
            this.nameUzLatin = dto.getCategoryDTO().getNameUzLatin();
        }
        this.xizmatType = dto.getXizmatType();
    }
}
