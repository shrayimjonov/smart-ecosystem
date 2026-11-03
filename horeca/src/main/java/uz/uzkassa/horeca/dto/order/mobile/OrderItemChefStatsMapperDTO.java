package uz.uzkassa.horeca.dto.order.mobile;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Slf4j
public class OrderItemChefStatsMapperDTO implements Serializable {


    @Serial
    private static final long serialVersionUID = 345466634L;

    Long preparingCount;

    Long readyCount;

    Long cancelledCount;

    public OrderItemChefStatsMapperDTO(Long preparingCount, Long ready, Long cancelled) {
        this.preparingCount = preparingCount;
        this.readyCount = ready;
        this.cancelledCount = cancelled;
    }
}
