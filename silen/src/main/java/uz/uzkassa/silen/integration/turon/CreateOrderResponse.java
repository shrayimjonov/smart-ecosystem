package uz.uzkassa.silen.integration.turon;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CreateOrderResponse implements Serializable {
    private String orderId;
    private Integer expectedCompleteTimestamp;
}
