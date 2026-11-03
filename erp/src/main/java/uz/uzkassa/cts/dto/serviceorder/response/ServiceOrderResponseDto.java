package uz.uzkassa.cts.dto.serviceorder.response;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.serviceorderinvoice.response.ServiceOrderInvoiceResponseDto;
import uz.uzkassa.cts.dto.tariff.response.TariffDeviceResponseDTO;
import uz.uzkassa.cts.enums.OrderStatus;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class ServiceOrderResponseDto {

    private Long id;
    @NotNull
    private List<TariffDeviceResponseDTO> deviceTariffs;
    @NotNull
    private Long contractId;
    private Long customerId;
    private String customerName;
    private String tin;
    private OrderStatus orderStatus;
    private Long specificationId;
    private List<ServiceOrderInvoiceResponseDto> invoices;

}
