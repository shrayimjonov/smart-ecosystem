package uz.uzkassa.cts.dto.orderspecification.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class OrderSpecificationRequestDto implements Serializable {

    private Long id;

    @NotNull
    private Long orderId;

    @NotNull
    private boolean calculateWithVat = false;
}
