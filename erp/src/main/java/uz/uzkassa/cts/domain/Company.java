package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.dto.company.CompanyCommonDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.interfaces.Auditable;

import java.io.Serial;

@Entity
@Table(name = "company",
        indexes = {
                @Index(columnList = "tin", name = "company_tin_idx"),
                @Index(columnList = "name", name = "company_name_idx"),
                @Index(columnList = "brand", name = "company_brand_idx"),
                @Index(columnList = "status", name = "company_status_idx")
        })
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE company SET deleted = 'true' WHERE id=?")
public class Company extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 5519298572L;

    @NotNull
    @Size(min = 9, max = 14)
    @Column(name = "tin", length = 9, unique = true, nullable = false)
    String tin;

    @NotNull
    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "brand")
    String brand;

    @Column(name = "oked", length = 5)
    String oked;

    @Column(name = "phone")
    String phone;

    @Column(name = "email")
    String email;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    CommonStatus status = CommonStatus.ACTIVE;

    @Column(name = "company_bank_id")
    Long companyBankId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_bank_id", updatable = false, insertable = false)
    CompanyBank companyBank;

    @Column(name = "vat_id")
    Long vatId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vat_id", updatable = false, insertable = false)
    Vat vat;

    public void setTin(String tin) {
        addChangeLog("tin", this.tin, tin);
        this.tin = tin;
    }

    public void setName(String name) {
        addChangeLog("name", this.name, name);
        this.name = name;
    }

    public void setBrand(String brand) {
        addChangeLog("brand", this.brand, brand);
        this.brand = brand;
    }

    public void setOked(String oked) {
        addChangeLog("oked", this.oked, oked);
        this.oked = oked;
    }

    public void setPhone(String phone) {
        addChangeLog("phone", this.phone, phone);
        this.phone = phone;
    }

    public void setEmail(String email) {
        addChangeLog("email", this.email, email);
        this.email = email;
    }

    public void setStatus(CommonStatus status) {
        addChangeLog("status", this.status != null ? this.status.getName() : null, status != null ? status.getName() : null);
        this.status = status;
    }

    public void setVat(Vat vat) {
        addChangeLog("vat", this.vat != null ? this.vat.getName() : null, vat != null ? vat.getName() : null);
        this.vat = vat;
    }

    public CompanyCommonDTO toShortDTO() {
        return new CompanyCommonDTO(getId(), getName(), getTin());
    }

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public String getEntityName() {
        return this.name;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.COMPANY;
    }
}
