package uz.uzkassa.horeca.dto.hallitem;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Setter
@Getter
@FieldDefaults(level = PRIVATE)
public class HallItemTableStatisticsDTO {

    Long free;

    Long active;

    Long booked;

    Long blocked;

    Long inActive;

    Long united;

    Long capacity;

    Float maxFee;

    Float minFee;

    public HallItemTableStatisticsDTO(Long free, Long active, Long booked, Long blocked, Long inActive, Long united, Long capacity, Float maxFee, Float minFee) {
        this.free = free;
        this.active = active;
        this.booked = booked;
        this.blocked = blocked;
        this.inActive = inActive;
        this.united = united;
        this.capacity = capacity;
        this.maxFee = maxFee;
        this.minFee = minFee;
    }
}
