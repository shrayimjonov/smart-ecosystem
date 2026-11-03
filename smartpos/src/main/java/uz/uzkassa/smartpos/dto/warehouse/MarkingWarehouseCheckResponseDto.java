package uz.uzkassa.smartpos.dto.warehouse;

import uz.uzkassa.smartpos.enums.MarkingLevel;

import java.io.Serializable;

public record MarkingWarehouseCheckResponseDto(String marking, MarkingLevel level) implements Serializable {
}