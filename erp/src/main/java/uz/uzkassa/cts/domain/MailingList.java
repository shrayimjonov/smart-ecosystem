package uz.uzkassa.cts.domain;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.interfaces.Auditable;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Table(name = "mailing_list")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE mailing_list SET deleted = 'true' WHERE id=?")
public class MailingList extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 3331035849L;

    @NotNull
    @Column(name = "name", nullable = false)
    String name;

    @NotNull
    @Column(name = "description", nullable = false)
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    String description;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    CommonStatus status = CommonStatus.ACTIVE;

    @Column(name = "member_count")
    Integer memberCount;

    @OneToMany(mappedBy = "mailingList", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = 'false'")
    @OrderBy("id")
    List<MailingListItem> items = new ArrayList<>();

    public void addItem(MailingListItem item) {
        item.setMailingList(this);
        item.setMailingListId(this.id);
        items.add(item);
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
        addChangeLog("name", this.name, name);
        this.name = name;
    }

    public void setDescription(String description) {
        addChangeLog("name", this.description, description);
        this.description = description;
    }

    public void setStatus(CommonStatus status) {
        addChangeLog("status", this.status != null ? this.status.getName() : null, status != null ? status.getName() : null);
        this.status = status;
    }

    public void setMemberCount(Integer memberCount) {
        addChangeLog("memberCount", this.memberCount != null ? this.memberCount.toString() : null, memberCount != null ? memberCount.toString() : null);
        this.memberCount = memberCount;
    }
}