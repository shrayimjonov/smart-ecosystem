package uz.uzkassa.smartpos.domain.postgres.company;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import uz.uzkassa.smartpos.dto.ActivityTypeDTO;
import uz.uzkassa.smartpos.enums.ProductGroup;

import java.io.Serializable;

/**
 * A ActivityType.
 */
@Entity
@Table(name = "activity_type")
@Getter
@Setter
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@DynamicUpdate
public class ActivityType implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code")
    private Long code;

    @Column(name = "name_ru")
    private String nameRu;

    @Column(name = "name_uz_cyrillic")
    private String nameUzCyrillic;

    @Column(name = "name_uz_latin")
    private String nameUzLatin;

    @Column(name = "parent_id")
    private Long parentId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", updatable = false, insertable = false, foreignKey = @ForeignKey(name = "activity_type_parent_fk"))
    private ActivityType parent;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_group")
    private ProductGroup productGroup;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ActivityType)) {
            return false;
        }
        return id != null && id.equals(((ActivityType) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    public ActivityTypeDTO getDTO() {
        return new ActivityTypeDTO(id, nameRu, parentId);
    }

    public uz.uzkassa.smartpos.integration.apay.ActivityTypeDTO toApayDTO() {
        return new uz.uzkassa.smartpos.integration.apay.ActivityTypeDTO(code != null ? code.toString() : null,
            getParent() != null ? getParent().toApayDTO() : null);
    }
}
