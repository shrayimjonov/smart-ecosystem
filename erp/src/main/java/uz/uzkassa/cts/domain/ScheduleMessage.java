package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.ChannelType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.ScheduleMessageStatus;
import uz.uzkassa.cts.interfaces.Auditable;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Table(name = "schedule_message",
        indexes = {
                @Index(columnList = "status", name = "schedule_message_status_idx"),
                @Index(columnList = "channel_type", name = "schedule_message_channel_type_idx"),
                @Index(columnList = "mailing_list_id", name = "schedule_message_mailing_list_id_idx"),
                @Index(columnList = "job_id", name = "schedule_message_job_id_idx"),
                @Index(columnList = "schedule_date", name = "schedule_message_date_idx")
        })
@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE schedule_message SET deleted = 'true' WHERE id=?")
public class ScheduleMessage extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 1656107790L;

    @NotNull
    @Column(name = "subject", nullable = false)
    String subject;

    @NotNull
    @Column(name = "message", nullable = false)
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    String message;

    @NotNull
    @Column(name = "schedule_date", nullable = false)
    LocalDateTime scheduleDate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    ScheduleMessageStatus status = ScheduleMessageStatus.ACTIVE;

    @NotNull
    @Column(name = "channel_type", nullable = false)
    @Enumerated(EnumType.STRING)
    ChannelType channelType;

    @NotNull
    @Column(name = "mailing_list_id", nullable = false)
    Long mailingListId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mailing_list_id", updatable = false, insertable = false)
    MailingList mailingList;

    @NotNull
    @Column(name = "job_id", nullable = false)
    String jobId;

    @Override
    public Long getEntityId() {
        return id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.SCHEDULE_MESSAGE;
    }

    public void setSubject(String subject) {
        addChangeLog("subject", this.subject, subject);
        this.subject = subject;
    }

    public void setMessage(String message) {
        addChangeLog("message", this.message, message);
        this.message = message;
    }

    public void setScheduleDate(LocalDateTime scheduleDate) {
        addChangeLog("scheduleDate", this.scheduleDate != null ? this.scheduleDate.toString() : null, scheduleDate != null ? scheduleDate.toString() : null);
        this.scheduleDate = scheduleDate;
    }

    public void setStatus(ScheduleMessageStatus status) {
        addChangeLog("status", this.status != null ? this.status.getName() : null, status != null ? status.getName() : null);
        this.status = status;
    }

    public void setChannelType(ChannelType channelType) {
        addChangeLog("channelType", this.channelType != null ? this.channelType.getName() : null, channelType != null ? channelType.getName() : null);
        this.channelType = channelType;
    }
}
