package uz.uzkassa.cts.domain.inventory;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.cts.domain.Branch;
import uz.uzkassa.cts.domain.Company;
import uz.uzkassa.cts.domain.base.LocalizedEntity;
import uz.uzkassa.cts.domain.hrms.Department;
import uz.uzkassa.cts.dto.category.admin.CategoryTreeAdminDTO;

import java.io.Serial;
import java.math.BigInteger;

@SqlResultSetMappings(
        {
                @SqlResultSetMapping(
                        name = "CategoryTreeDTOMapper",
                        classes = @ConstructorResult(
                                targetClass = CategoryTreeAdminDTO.class,
                                columns = {
                                        @ColumnResult(name = "id", type = Long.class),
                                        @ColumnResult(name = "name", type = String.class),
                                        @ColumnResult(name = "parentId", type = Long.class),
                                        @ColumnResult(name = "productCount", type = BigInteger.class)
                                }
                        )
                )
        }
)
@Entity
@Table(name = "category", indexes = {
        @Index(columnList = "company_id", name = "category_company_id_idx"),
        @Index(columnList = "parent_id", name = "category_parent_id_idx")
})
@Getter
@Setter
@SQLDelete(sql = "UPDATE category SET deleted = 'true' WHERE id=?")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Category extends LocalizedEntity {

    @Serial
    private static final long serialVersionUID = 1234567895826L;

    @Column(name = "parent_id")
    Long parentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", updatable = false, insertable = false)
    Category parent;

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;
}
