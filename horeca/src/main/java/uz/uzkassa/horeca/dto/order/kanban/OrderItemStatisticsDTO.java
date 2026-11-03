package uz.uzkassa.horeca.dto.order.kanban;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 13.05.2024
 */
@Getter
@Setter
@NoArgsConstructor
public class OrderItemStatisticsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 435158934663L;

    Long preparingCount;

    Long readyCount;

    Long cancelledCount;

    Long newCount;

    public OrderItemStatisticsDTO(Long preparingCount, Long readyCount, Long cancelledCount, Long newCount) {
        this.preparingCount = preparingCount;
        this.readyCount = readyCount;
        this.cancelledCount = cancelledCount;
        this.newCount = newCount;
    }
}
