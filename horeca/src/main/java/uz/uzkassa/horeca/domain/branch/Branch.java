package uz.uzkassa.horeca.domain.branch;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.horeca.domain.Category;
import uz.uzkassa.horeca.domain.FileUpload;
import uz.uzkassa.horeca.domain.address.Address;
import uz.uzkassa.horeca.domain.auth.User;
import uz.uzkassa.horeca.domain.base.BaseEntity;
import uz.uzkassa.horeca.domain.company.Company;
import uz.uzkassa.horeca.domain.hall.Hall;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.enums.Vat;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "branch",
        indexes = {
                @Index(columnList = "name", name = "branch_name_idx"),
                @Index(columnList = "company_id", name = "branch_company_id_idx"),
                @Index(columnList = "region_id", name = "branch_region_id_idx"),
                @Index(columnList = "district_id", name = "branch_district_id_idx"),
                @Index(columnList = "neighborhood_id", name = "branch_neighborhood_id_idx")
        })
@SQLDelete(sql = "UPDATE branch set deleted='true' WHERE" +
        "id=?")
public class Branch extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 8L;

    @Column(name = "cts_id", unique = true)
    Long ctsId;

    @NotNull
    @Column(name = "name", nullable = false)
    String name;

    @Embedded
    Address address;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    CommonStatus status = CommonStatus.ACTIVE;

    @Column(name = "photo_id")
    Long photoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id", insertable = false, updatable = false)
    FileUpload photo;

    @Column(name = "is_main", columnDefinition = "boolean default false")
    boolean isMain;

    @Column(name = "manager_id")
    Long managerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", updatable = false, insertable = false)
    User manager;

    @NotNull
    @Column(name = "company_id", nullable = false)
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = 'false'")
    @OrderBy("id")
    List<Hall> halls = new ArrayList<>();

    @OneToMany(mappedBy = "branch")
    @SQLRestriction("deleted = 'false'")
    @OrderBy("id")
    List<Category> category;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Branch)) {
            return false;
        }
        return id != null && id.equals(((Branch) o).id);
    }
}
