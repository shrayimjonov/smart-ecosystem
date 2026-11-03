package uz.uzkassa.smartpossupply.domain.company;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.smartpossupply.domain.base.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "company_permissions")
@SQLDelete(sql = "UPDATE company_permissions SET deleted = true WHERE id=?")
public class CompanyPermission extends BaseEntity {
    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Column(name = "service_type")
    private String serviceType;

    @Column(name = "start_date")
    LocalDateTime startDate;

    @Column(name = "end_date")
    LocalDateTime endDate; // end date on billing

    @Column(name = "unit_count")
    Integer unitCount;

    @Column(name = "amount")
    BigDecimal amount;

    @Column(name = "agreement_id")
    Long agreementId;

    @Column(name = "xizmat_id")
    Long xizmatId;

    @Column(name = "title")
    String title;

    @Column(name = "title_uz")
    String titleUz;

    @Column(name = "xizmat_type")
    String xizmatType;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompanyPermission that = (CompanyPermission) o;
        return Objects.equals(companyId, that.companyId)
                && Objects.equals(serviceType, that.serviceType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(companyId, serviceType);
    }

}
