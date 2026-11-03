package uz.uzkassa.cts.dto.order.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class OrderDto implements Serializable {

    private Long id;
    @NotNull
    private String fullName;
    @NotNull
    private String phoneNumber;
    @NotNull
    private List<OrderItemDto> itemDtos;
    @NotNull
    private Long contractId;

}
