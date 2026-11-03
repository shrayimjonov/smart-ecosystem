package uz.uzkassa.silen.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.ReferenceDTO;

import java.io.Serial;
import java.io.Serializable;

/**
 * A Reference.
 */
@Getter
@Setter
@Entity
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "reference", uniqueConstraints = {
        @UniqueConstraint(columnNames = "code"),
    @UniqueConstraint(columnNames = {"code","name", "parent_id", "deleted"})
})
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@SQLDelete(sql = "UPDATE reference SET deleted = 'true' WHERE id=?")
public class Reference extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "code", unique = true)
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "parent_id")
    private String parentId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", referencedColumnName = "code", updatable = false, insertable = false)
    private Reference parent;

    public ReferenceDTO toDto(){
        ReferenceDTO dto = new ReferenceDTO();
        dto.setId(getId());
        dto.setCode(getCode());
        dto.setName(getName());
        dto.setParentId(getParentId());
        if (getParent() != null) {
            dto.setParentName(getParent().getName());
        }
        return dto;
    }

    @Override
    public boolean isNew() {
        return getId() == null;
    }
}
