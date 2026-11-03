package uz.uzkassa.smartpossupply.dto.order.hippo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class OrderHippoDTO implements Serializable {

    String orderNumber;

    String marketCode;

    LocalDateTime orderDate;

    String description;

    String customerTin;

    String contractNumber;
    LocalDate contractDate;

    List<OrderProductHippoDTO> products = new ArrayList<>();

    public void addProduct(OrderProductHippoDTO orderProductHippoDTO) {
        products.add(orderProductHippoDTO);
    }
}
