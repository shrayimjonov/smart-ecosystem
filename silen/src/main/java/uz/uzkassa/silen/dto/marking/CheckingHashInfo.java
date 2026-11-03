package uz.uzkassa.silen.dto.marking;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@EqualsAndHashCode
public class CheckingHashInfo {
    String productId;
    String serialNumber;
    Integer quantity;
    LocalDateTime expirationDate;
    LocalDateTime productionDate;
}
