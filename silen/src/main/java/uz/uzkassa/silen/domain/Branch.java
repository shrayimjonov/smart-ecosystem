package uz.uzkassa.silen.domain;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.enumeration.BranchStatus;


@Entity
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "branch")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@SQLDelete(sql = "update branch set deleted='true' where id=?")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Branch extends BaseOrganizationEntity {

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BranchStatus status;

    @Column(name = "district_id")
    private String districtId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id", updatable = false, insertable = false)
    private District district;

    private String address;


    private Double lat;

    private Double lon;

    @Column(name = "main", columnDefinition = "boolean default false")
    private boolean main = false;

    @Column(name = "branch_code")
    private String branchCode;

    @Column(name = "manager_id")
    private String managerId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", updatable = false, insertable = false)
    private User manager;

}
