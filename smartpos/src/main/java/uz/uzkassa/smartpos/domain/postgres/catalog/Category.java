package uz.uzkassa.smartpos.domain.postgres.catalog;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import uz.uzkassa.smartpos.domain.postgres.base.audit.AbstractAuditingEntity;
import uz.uzkassa.smartpos.dto.CategoryDTO;
import uz.uzkassa.smartpos.enums.CompanyType;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "category")
@NoArgsConstructor
@DynamicUpdate
public class Category extends AbstractAuditingEntity {
    @Id
    @SequenceGenerator(name = "category_id_sequence", sequenceName = "category_id_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_id_sequence")
    private Long id;

    @NotNull
    @Column(name = "name")
    private String name;

    @Column(name = "name_uz")
    private String nameUz;

    @Column(name = "parent_id")
    private Long parentId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Category parent;

    @Enumerated(EnumType.STRING)
    @Column(name = "company_type")
    private CompanyType companyType;

    @Column(name = "mxik_code")
    private String mxikCode;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Category> childCategories;

    public CategoryDTO getDTO() {
        final CategoryDTO dto = new CategoryDTO(id, name, parentId);
        dto.setNameUz(getNameUz());
        dto.setCompanyType(getCompanyType());
        dto.setMxikCode(getMxikCode());
        return dto;
    }
}
