package uz.uzkassa.silen.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.NotificationDTO;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.enumeration.NotificationStatus;
import uz.uzkassa.silen.enumeration.NotificationTemplateType;

import java.io.Serial;
import java.io.Serializable;

/**
 * A Notification.
 */
@Getter
@Setter
@Entity
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "notification")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@SQLDelete(sql = "UPDATE notification SET deleted = 'true' WHERE id=?")
public class Notification extends BaseOrganizationEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status = NotificationStatus.INFO;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private NotificationTemplateType type;

    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "seen", columnDefinition = "boolean default false")
    private boolean seen = false;

    @Column(name = "contents", length = 1000)
    private String contents;

    public NotificationDTO toDto() {
        NotificationDTO dto = new NotificationDTO();
        dto.setOrganizationId(getOrganizationId());
        if (getOrganization() != null) {
            dto.setOrganizationName(getOrganization().getName());
        }
        dto.setId(getId());

        dto.setStatus(getStatus());
        if (getStatus() != null) {
            dto.setStatusName(getStatus().getText());
        }
        dto.setSeen(isSeen());
        dto.setCreatedDate(getCreatedDate());
        dto.setContents(getContents());
        dto.setName(getName());
        dto.setDescription(getDescription());
        dto.setType(getType());
        if (getType() != null) {
            dto.setTypeName(getType().getText());
        }

        return dto;
    }

    @Override
    public SelectItem toSelectItem() {
        return new SelectItem(getId(), getName(), getDescription());
    }
}
