package uz.uzkassa.silen.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.NotificationTemplateDTO;
import uz.uzkassa.silen.enumeration.NotificationStatus;
import uz.uzkassa.silen.enumeration.NotificationTemplateType;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Slf4j
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "notification_template",
    indexes = {
        @Index(name = "template_type", columnList = "type")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "template_type", columnNames = "type")
    })
@SQLDelete(sql = "UPDATE notification_template SET deleted = 'true' WHERE id=?")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class NotificationTemplate extends BaseOrganizationEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private NotificationTemplateType type;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "contents", length = 1000)
    private String contents;

    public NotificationTemplateDTO toDto() {
        NotificationTemplateDTO dto = new NotificationTemplateDTO();
        dto.setId(getId());
        dto.setName(getName());
        dto.setDescription(getDescription());
        dto.setContents(this.getContents());
        dto.setStatus(getStatus());
        if (getStatus() != null) {
            dto.setStatusName(getStatus().getText());
        }
        dto.setType(getType());
        if (getType() != null) {
            dto.setTypeName(getType().getText());
        }
        dto.setCreatedDate(getCreatedDate());
        return dto;
    }
}
