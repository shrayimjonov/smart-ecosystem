package uz.uzkassa.cts.dto.order.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class OrderItemDto implements Serializable {

    private Long id;
    @NotNull
    private Long count;
    @NotNull
    private Long modelId;
}
