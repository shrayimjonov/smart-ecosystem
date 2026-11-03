package uz.uzkassa.cts.domain.hrms;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.enums.ChannelType;
import uz.uzkassa.cts.enums.EntityTypeEnum;

import java.io.Serial;
import java.io.Serializable;

/**
 * Created date time: 16.01.2025 16:07
 */

@Entity
@Table(name = "reminder",
        indexes = {
                @Index(columnList = "entity_id", name = "reminder_entity_id_idx"),
                @Index(columnList = "entity_type", name = "reminder_entity_type_idx"),
                @Index(columnList = "job_id", name = "reminder_job_id_idx"),
                @Index(columnList = "code", name = "reminder_code_idx")
        })
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Reminder extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 35612387954L;

    @NotNull
    @Column(name = "entity_id", nullable = false)
    Long entityId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false)
    EntityTypeEnum entityType;

    @NotNull
    @Column(name = "code", nullable = false)
    String code;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    ChannelType type;

    @NotNull
    @Column(name = "job_id", nullable = false)
    String jobId;
}
