package uz.uzkassa.cts.domain.timeslot;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.TimeslotType;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "timeslot",
        indexes = {
                @Index(columnList = "name", name = "timeslot_name_idx"),
                @Index(columnList = "entity_id", name = "timeslot_entity_id_idx"),
                @Index(columnList = "entity_type", name = "timeslot_entity_type_idx"),
                @Index(columnList = "entity_id, entity_type", name = "timeslot_entity_id_entity_type_idx")
        })
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE timeslot SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class Timeslot extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 859348L;

    @NotNull
    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    CommonStatus status = CommonStatus.ACTIVE;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    TimeslotType type = TimeslotType.STANDARD;

    @Column(name = "working_hour")
    Integer workingHour;

    @Column(name = "lunch_hour")
    Integer lunchHour;

    @Column(name = "from_time")
    LocalTime fromTime;

    @Column(name = "to_time")
    LocalTime toTime;

    @Column(name = "entity_id")
    Long entityId;

    @Column(name = "entity_type")
    @Enumerated(EnumType.STRING)
    EntityTypeEnum entityType;

    @Column(name = "round_the_clock", columnDefinition = "boolean default false")
    boolean roundTheClock = false;

    @OneToMany(mappedBy = "timeslot", cascade = CascadeType.ALL, orphanRemoval = true)
    List<TimeslotItem> items = new ArrayList<>();

    public void addItem(TimeslotItem timeslotItem) {
        timeslotItem.setTimeslot(this);
        timeslotItem.setTimeslotId(this.id);
        items.add(timeslotItem);
    }

    public List<TimeslotItem> getItems() {
        return items.stream().sorted((o1, o2) -> (o1.getDay() != null && o2.getDay() != null) ? o1.getDay().compareTo(o2.getDay()) : 0).toList();
    }
}
