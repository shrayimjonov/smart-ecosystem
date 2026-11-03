package uz.uzkassa.smartpossupply.dto.user.supervisor;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExpeditorStatisticsDTO implements Serializable {
    Long countOfExpeditors;
    ExpeditorCompletedStockOutDTO orderStatistics;
}
