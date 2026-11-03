package uz.uzkassa.smartpossupply.dto.payload;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.CodeType;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 10.01.2025
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockOptimizationPayload implements Serializable {

    CodeType codeType;

    LocalDateTime fromDate;

    LocalDateTime toDate;

    public StockOptimizationPayload(CodeType codeType, LocalDateTime fromDate, LocalDateTime toDate) {
        this.codeType = codeType;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }
}
