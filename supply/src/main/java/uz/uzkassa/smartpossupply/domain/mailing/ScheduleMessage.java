package uz.uzkassa.smartpossupply.domain.mailing;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.enums.AppCategory;
import uz.uzkassa.smartpossupply.enums.ChannelType;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.ScheduleMessageStatus;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.io.Serial;
import java.time.LocalDateTime;

@Table(name = "schedule_message",
    indexes = {
        @Index(columnList = "status", name = "schedule_message_status_idx"),
        @Index(columnList = "mailing_list_id", name = "schedule_message_mailing_list_id_idx"),
        @Index(columnList = "job_id", name = "schedule_message_job_id_idx"),
        @Index(columnList = "schedule_date", name = "schedule_message_date_idx")
    })
@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE schedule_message SET deleted = true WHERE id=?")
@SQLRestriction(value = "deleted = false")
public class ScheduleMessage extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 1656107790L;

    @Column(name = "subject_uz")
    String subjectUz;

    @Column(name = "subject_cyrillic")
    String subjectCyrillic;

    @Column(name = "subject_ru")
    String subjectRu;

    @Column(name = "message_uz")
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    String messageUz;

    @Column(name = "message_cyrillic")
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    String messageCyrillic;

    @Column(name = "message_ru")
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    String messageRu;

    @NotNull
    @Column(name = "channel_type", nullable = false)
    @Enumerated(EnumType.STRING)
    ChannelType channelType;

    @NotNull
    @Column(name = "schedule_date", nullable = false)
    LocalDateTime scheduleDate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    ScheduleMessageStatus status = ScheduleMessageStatus.ACTIVE;

    @Column(name = "app_category")
    @Enumerated(EnumType.STRING)
    AppCategory appCategory;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mailing_list_id", nullable = false, updatable = false)
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

    public void setSubjectUz(String subjectUz) {
        addLogHistory("subjectUz", this.subjectUz, subjectUz);
        this.subjectUz = subjectUz;
    }

    public void setSubjectCyrillic(String subjectCyrillic) {
        addLogHistory("subjectCyrillic", this.subjectCyrillic, subjectCyrillic);
        this.subjectCyrillic = subjectCyrillic;
    }

    public void setSubjectRu(String subjectRu) {
        addLogHistory("subjectRu", this.subjectRu, subjectRu);
        this.subjectRu = subjectRu;
    }

    public void setMessageUz(String messageUz) {
        addLogHistory("messageUz", this.messageUz, messageUz);
        this.messageUz = messageUz;
    }

    public void setMessageCyrillic(String messageCyrillic) {
        addLogHistory("messageCyrillic", this.messageCyrillic, messageCyrillic);
        this.messageCyrillic = messageCyrillic;
    }

    public void setMessageRU(String messageRU) {
        addLogHistory("messageRU", this.messageRu, messageRU);
        this.messageRu = messageRU;
    }


    public void setScheduleDate(LocalDateTime scheduleDate) {
        addLogHistory("scheduleDate", this.scheduleDate != null ? this.scheduleDate.toString() : null, scheduleDate != null ? scheduleDate.toString() : null);
        this.scheduleDate = scheduleDate;
    }

    public void setStatus(ScheduleMessageStatus status) {
        addLogHistory("status", this.status != null ? this.status.getName() : null, status != null ? status.getName() : null);
        this.status = status;
    }

    public void setChannelType(ChannelType channelType) {
        addLogHistory("channelType", this.channelType != null ? this.channelType.getName() : null, channelType != null ? channelType.getName() : null);
        this.channelType = channelType;
    }
}
