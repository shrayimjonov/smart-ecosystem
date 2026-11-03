package uz.uzkassa.smartpossupply.domain.checklist;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.smartpossupply.domain.base.BaseEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;

import java.io.Serial;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "condition", indexes = {
        @Index(columnList = "company_id", name = "condition_company_id_idx"),
        @Index(columnList = "deleted", name = "condition_deleted_idx")
})
@SQLDelete(sql = "UPDATE condition SET deleted = true WHERE id=?")
@SQLRestriction(value = "deleted = false")
public class Condition extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 243457567L;

    @NotNull
    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "company_id", nullable = false)
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;
}
