package uz.uzkassa.smartpos.domain.postgres.catalog;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import uz.uzkassa.smartpos.domain.postgres.base.audit.AbstractAuditingEntity;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.dto.CategoryDTO;

import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "category_branch")
@IdClass(CategoryBranchId.class)
@NoArgsConstructor
@DynamicUpdate
public class CategoryBranch extends AbstractAuditingEntity {

    @Id
    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Id
    @Column(name = "branch_id", nullable = false)
    private Long branchId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", updatable = false, insertable = false)
    private Branch branch;

    @Column(name = "enabled", columnDefinition = "boolean default true")
    private boolean enabled = true;

    private String name;
    @Column(name = "name_uz")
    private String nameUz;

    @Column(name = "mxik_code")
    private String mxikCode;

    @Column(name = "parent_id")
    private Long parentId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns(value = {
            @JoinColumn(name = "parent_id", referencedColumnName = "category_id", insertable = false, updatable = false),
            @JoinColumn(name = "branch_id", referencedColumnName = "branch_id", insertable = false, updatable = false)
    }, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private CategoryBranch parent;

    @OneToMany(mappedBy = "parent")
    private List<CategoryBranch> childCategories;

    public CategoryBranch(CategoryBranch categoryBranch, Long branchId) {
        this.categoryId = categoryBranch.getCategoryId();
        this.branchId = branchId;
        this.name = categoryBranch.getName();
        this.nameUz = categoryBranch.getNameUz();
        this.mxikCode = categoryBranch.getMxikCode();
        this.parentId = categoryBranch.getParentId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CategoryBranch that = (CategoryBranch) o;
        return Objects.equals(categoryId, that.categoryId) &&
                Objects.equals(branchId, that.branchId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryId, branchId);
    }

    @Override
    public CategoryBranchId getId() {
        return new CategoryBranchId(categoryId, branchId);
    }


    public CategoryDTO getDTO() {
        final CategoryDTO dto = new CategoryDTO(categoryId, name, parentId);
        dto.setMxikCode(mxikCode);
        return dto;
    }

}
