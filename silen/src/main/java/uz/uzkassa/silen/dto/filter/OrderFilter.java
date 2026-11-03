package uz.uzkassa.silen.dto.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.BufferStatus;
import uz.uzkassa.silen.enumeration.CisType;

import java.util.List;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderFilter extends Filter {

    String productId;

    String orderProductId;

    BufferStatus status;

    List<BufferStatus> statusIn;

    CisType cisType;

    List<String> parties;
}
