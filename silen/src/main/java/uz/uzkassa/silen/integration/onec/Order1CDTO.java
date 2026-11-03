package uz.uzkassa.silen.integration.onec;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@ToString
@Getter
@Setter
@NoArgsConstructor
public class Order1CDTO implements Serializable {

    String orderNumber;

    LocalDate orderDate;

    String description;

    Long branchId;

    Long warehouseId;

    String customerTin;

    OrderContract1CDTO contract;

    List<OrderProduct1CDTO> products;
}
