package uz.uzkassa.horeca.domain.company;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.horeca.constants.Constants;
import uz.uzkassa.horeca.domain.ActivityType;
import uz.uzkassa.horeca.domain.address.Address;
import uz.uzkassa.horeca.domain.auth.User;
import uz.uzkassa.horeca.domain.base.AbstractAuditingEntity;
import uz.uzkassa.horeca.domain.billing.UsagePlan;
import uz.uzkassa.horeca.domain.branch.Branch;
import uz.uzkassa.horeca.enums.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "company",
        indexes = {
                @Index(columnList = "name", name = "company_name_idx"),
                @Index(columnList = "tin", name = "company_tin_idx"),
                @Index(columnList = "region_id", name = "company_region_id_idx"),
                @Index(columnList = "district_id", name = "company_district_id_idx"),
                @Index(columnList = "neighborhood_id", name = "company_neighborhood_id_idx"),
                @Index(columnList = "activity_type_id", name = "company_activity_type_id_idx"),
                @Index(columnList = "owner_id", name = "company_owner_id_idx")
        })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@SQLDelete(sql = "UPDATE company set deleted='true' where id=?")
public class Company extends AbstractAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 9L;

    @Column(name = "cts_id", unique = true)
    Long ctsId;

    @NotNull
    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "brand")
    String brand;

    @NotNull
    @Column(name = "tin", unique = true, length = 14, nullable = false)
    @Pattern(regexp = Constants.TIN_PINFL_REGEX)
    String tin;

    @Column(name = "oked")
    String oked;

    @Column(name = "wifi_password")
    String wifiPassword;

    @Column(name = "contact_phone")
    String contactPhone;

    @Column(name = "contact_name")
    String contactName;

    @Enumerated(EnumType.STRING)
    @Column(name = "business_type")
    BusinessType businessType;

    @Column(name = "vat")
    @Enumerated(value = EnumType.STRING)
    Vat vat;

    @Embedded
    Address address;

    @Column(name = "owner_id")
    Long ownerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", updatable = false, insertable = false)
    User owner;

    @Column(name = "registration_date")
    LocalDateTime registrationDate;

    @Column(name = "activity_type_id")
    Long activityTypeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_type_id", insertable = false, updatable = false)
    ActivityType activityType;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    CommonStatus status;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = 'false'")
    List<Branch> branches = new ArrayList<>();

    @Column(name = "apay_id")
    String apayId;

    @Column(name = "apay_tariff_id")
    String apayTariffId;

    @Enumerated(EnumType.STRING)
    @Column(name = "apay_application_status")
    ApayApplicationStatus apayApplicationStatus;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<UsagePlan> usagePlans = new HashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "company_permissions", joinColumns = {@JoinColumn(name = "company_id", referencedColumnName = "id")})
    @Column(name = "expire_date")
    Map<String, LocalDateTime> permissions = new HashMap<>();

    @Column(name = "free_trial", columnDefinition = "boolean default false")
    boolean freeTrial = false;

    public void addUsagePLan(UsagePlan usagePlan) {
        usagePlan.setCompany(this);
        usagePlans.add(usagePlan);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Company)) {
            return false;
        }
        return id != null && id.equals(((Company) o).id);
    }

    @Override
    public Long getEntityId() {
        return id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.COMPANY;
    }
}
