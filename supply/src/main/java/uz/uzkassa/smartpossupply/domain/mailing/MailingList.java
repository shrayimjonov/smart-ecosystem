package uz.uzkassa.smartpossupply.domain.mailing;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Table(name = "mailing_list",
    indexes = {
        @Index(columnList = "company_id", name = "mailing_list_company_id_idx")
    })
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE mailing_list SET deleted = true WHERE id=?")
@SQLRestriction(value = "deleted = false")
public class MailingList extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 5465449L;

    @NotNull
    @Column(name = "name", nullable = false)
    String name;

    @NotNull
    @Column(name = "description", nullable = false, length = 500)
    String description;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    CommonStatus status = CommonStatus.ACTIVE;

    @Column(name = "member_count", columnDefinition = "int4 default 0")
    Integer memberCount = 0;

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @OneToMany(mappedBy = "mailingList", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    List<MailingListItem> items = new ArrayList<>();

    public void setItems(List<MailingListItem> items) {
        this.items = items;
        this.memberCount = items.size();
    }

    @Override
    public Long getEntityId() {
        return id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.MAILING_LIST;
    }

    @Override
    public String getEntityName() {
        return this.name;
    }

    public void setName(String name) {
        addLogHistory("name", this.name, name);
        this.name = name;
    }

    public void setDescription(String description) {
        addLogHistory("name", this.description, description);
        this.description = description;
    }

    public void setStatus(CommonStatus status) {
        addLogHistory("status", this.status != null ? this.status.getName() : null, status != null ? status.getName() : null);
        this.status = status;
    }

    public void setMemberCount(Integer memberCount) {
        addLogHistory("memberCount", this.memberCount != null ? this.memberCount.toString() : null, memberCount != null ? memberCount.toString() : null);
        this.memberCount = memberCount;
    }
}