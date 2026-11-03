package uz.uzkassa.smartpossupply.web.rest.hippo;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.order.hippo.OrderHippoDTO;
import uz.uzkassa.smartpossupply.service.OrderService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.hippoOrderRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class HippoOrderResource {

    OrderService orderService;

    @Operation(summary = ApiConstants.Create)
    @PostMapping
    public ResponseEntity<Long> create(@RequestBody OrderHippoDTO orderDTO) {
        return ResponseEntity.ok(orderService.create(orderDTO));
    }
}
