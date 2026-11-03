package uz.uzkassa.cts.domain.hrms;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.domain.base.SimpleEntity;

import java.io.Serial;
import java.io.Serializable;

/**
 * Created date time: 26.12.2024 16:14
 */

@Entity
@Table(name = "event_share",
        indexes = {
                @Index(columnList = "event_id", name = "event_share_event_id_idx"),
                @Index(columnList = "participant_id", name = "event_share_participant_id_idx")
        })
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventShare extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 4578923490L;

    @Column(name = "event_id", nullable = false)
    Long eventId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", updatable = false, insertable = false)
    Event event;

    @Column(name = "participant_id", nullable = false)
    Long participantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", updatable = false, insertable = false)
    User participant;
}
