package uz.uzkassa.cts.domain.timeslot;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.domain.base.SimpleEntity;

import java.io.Serial;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "timeslot_item",
        indexes = {
                @Index(columnList = "timeslot_id", name = "timeslot_item_timeslot_id_idx"),
                @Index(columnList = "day", name = "timeslot_item_day_idx")
        })
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TimeslotItem extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 456333L;

    @NotNull
    @Column(name = "day", nullable = false)
    @Enumerated(EnumType.STRING)
    DayOfWeek day;

    @Column(name = "from_time")
    LocalTime fromTime;

    @Column(name = "to_time")
    LocalTime toTime;

    @Column(name = "lunch_from_time")
    LocalTime lunchFromTime;

    @Column(name = "lunch_to_time")
    LocalTime lunchToTime;

    @NotNull
    @Column(name = "timeslot_id", nullable = false)
    Long timeslotId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timeslot_id", updatable = false, insertable = false)
    Timeslot timeslot;
}
