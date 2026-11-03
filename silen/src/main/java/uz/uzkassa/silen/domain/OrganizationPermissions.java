package uz.uzkassa.silen.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import uz.uzkassa.silen.config.DbConstants;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * A Factory.
 */
@Getter
@Setter
@Entity
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "org_permissions",
    indexes = {
        @Index(name = "org_permissions_organization_id_idx", columnList = "org_id")
    })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class OrganizationPermissions implements Serializable {

    @Serial
    private static final long serialVersionUID = 1231212213441L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "org_id")
    private String orgId;

    @Column(name = "permissions_key")
    private String permissionsKey;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id", updatable = false, insertable = false)
    private Organization organization;

    @Column(columnDefinition = "int default 0")
    private Integer count = 0;

    @Column(name = "expire_date")
    private LocalDateTime expireDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrganizationPermissions that = (OrganizationPermissions) o;
        return orgId.equals(that.orgId) && permissionsKey.equals(that.permissionsKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orgId, permissionsKey);
    }

    @Override
    public String toString() {
        return "OrganizationPermission{" +
            "id='" + id + '\'' +
            ", organizationId='" + orgId + '\'' +
            ", serviceType='" + permissionsKey + '\'' +
            ", count=" + count +
            ", expireDate=" + expireDate +
            '}';
    }
}
