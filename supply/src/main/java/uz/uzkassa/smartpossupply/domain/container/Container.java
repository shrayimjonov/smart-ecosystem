package uz.uzkassa.smartpossupply.domain.container;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.customerbranch.CustomerBranch;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "container",
        indexes = {
                @Index(columnList = "company_id", name = "container_company_id_idx"),
                @Index(columnList = "identification_number", name = "container_identification_number_idx")
        }
)
@SQLDelete(sql = "UPDATE container SET deleted = true WHERE id=?")
@SQLRestriction(value = "deleted = false")
public class Container extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 84345011239L;

    @NotNull
    @Column(name = "identification_number", nullable = false)
    String identificationNumber;

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Column(name = "customer_id")
    Long customerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", updatable = false, insertable = false)
    Company customer;

    @Column(name = "customer_branch_id")
    Long customerBranchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_branch_id", updatable = false, insertable = false)
    CustomerBranch customerBranch;

    @Column(name = "special_tage")
    String specialTag;

    @Column(name = "type")
    String type;

    @Column(name = "width")
    Double width;

    @Column(name = "height")
    Double height;

    @Column(name = "depth")
    Double depth;

    @Column(name = "diameter")
    Double diameter;

    @Column(name = "capacity")
    Double capacity;

    @Column(name = "weight")
    Double weight;

    @Column(name = "material")
    String material;

    @Column(name = "comment", length = 1000)
    String comment;

    @OneToMany(mappedBy = "container", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    List<ContainerItem> items = new ArrayList<>();

    public void addItem(ContainerItem containerItem) {
        containerItem.setContainer(this);
        containerItem.setContainerId(this.id);
        items.add(containerItem);
    }
}
