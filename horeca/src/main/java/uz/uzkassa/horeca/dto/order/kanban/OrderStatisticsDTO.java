package uz.uzkassa.horeca.dto.order.kanban;

import java.io.Serial;
import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 13.05.2024
 */
@Getter
@Setter
@NoArgsConstructor
public class OrderStatisticsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 435158934663L;

    Long activeCount;

    Long readyCount;

    Long cancelledCount;

    public OrderStatisticsDTO(Long activeCount, Long readyCount, Long cancelledCount) {
        this.activeCount = activeCount;
        this.readyCount = readyCount;
        this.cancelledCount = cancelledCount;
    }
}
