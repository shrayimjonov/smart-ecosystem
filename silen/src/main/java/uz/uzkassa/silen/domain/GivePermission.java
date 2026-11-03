package uz.uzkassa.silen.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.GivePermissionDTO;
import uz.uzkassa.silen.enumeration.ServiceType;

import java.io.Serial;
import java.io.Serializable;

@ToString
@Getter
@Setter
@Entity
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "give_permission",
    indexes = {
        @Index(columnList = "organization_id", name = "give_permission_organization_id_idx"),
        @Index(columnList = "child_id", name = "give_permission_child_id_idx")
    })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class GivePermission extends BaseOrganizationEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1234124245L;

    private Integer quantity;

    @Column(name = "child_id")
    private String childId;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_type")
    private ServiceType fromType;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_type")
    private ServiceType toType;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Organization child;

    public GivePermissionDTO toDto() {
        GivePermissionDTO dto = new GivePermissionDTO();
        setAuditValues(dto);
        dto.setCreatedDate(getCreatedDate());
        dto.setQuantity(getQuantity());
        /*if (getChild() != null)
            dto.setChild(getChild().toSelectItem());*/
        if (getFromType() != null) {
            dto.setFrom(getFromType());
            dto.setFromName(getFromType().getNameRu());
        }
        dto.setParentId(getOrganizationId());
        dto.setChildId(getChildId());
        if (getToType() != null) {
            dto.setTo(getToType());
            dto.setToName(getToType().getNameRu());
        }
        return dto;
    }

}
