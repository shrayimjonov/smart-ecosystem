package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;

import java.io.Serial;

@Entity
@Table(name = "device_model",
        indexes = {
                @Index(columnList = "name", name = "device_model_name_idx"),
                @Index(columnList = "code", name = "device_model_code_idx"),
                @Index(columnList = "ofd_id", name = "device_model_ofd_id_idx"),
                @Index(columnList = "company_id", name = "device_model_company_id_idx"),
        })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
@SQLDelete(sql = "UPDATE device_model SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeviceModel extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 3209052600L;

    @Column(name = "name")
    String name;

    @Column(name = "code")
    String code;

    @Column(name = "ofd_id")
    Long ofdId;

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;
}
