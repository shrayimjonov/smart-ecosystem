package uz.uzkassa.silen.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uz.uzkassa.silen.config.ApplicationProperties;
import uz.uzkassa.silen.dto.ProductSelectItem;
import uz.uzkassa.silen.dto.SerialSelectItemDTO;
import uz.uzkassa.silen.dto.filter.ProductFilter;
import uz.uzkassa.silen.dto.filter.SerialNumberFilter;
import uz.uzkassa.silen.dto.marking.AggregationCreateDTO;
import uz.uzkassa.silen.enumeration.ProductStatus;
import uz.uzkassa.silen.service.AggregationService;
import uz.uzkassa.silen.service.ProductService;
import uz.uzkassa.silen.service.SerialNumberService;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicResource {
    private final AggregationService aggregationService;
    private final ProductService productService;
    private final SerialNumberService serialNumberService;
    private final ApplicationProperties applicationProperties;

    @GetMapping("/products")
    public ResponseEntity<List<ProductSelectItem>> getItems(@ModelAttribute final ProductFilter filter) {
        filter.setStatus(ProductStatus.ACCEPTED);
        return ResponseEntity.ok(productService.getProductItems(filter));
    }

    @GetMapping("/serial-numbers")
    public ResponseEntity<List<SerialSelectItemDTO>> getSerials(@ModelAttribute final SerialNumberFilter serialNumberFilter) {
        validateRequest();
        return ResponseEntity.ok(serialNumberService.selectItems(serialNumberFilter));
    }

    @PostMapping("/aggregation")
    public ResponseEntity<String> createAggregation(@RequestBody @Valid final AggregationCreateDTO dto) {
        validateRequest();
        dto.setOrganizationId("fa09c50d-8d2d-44e6-b742-dcf63da642d2"); //TODO temporary set for RADIKS
        return ResponseEntity.ok(aggregationService.create(dto, false));
    }

    private void validateRequest() {
        final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            final String value = (((ServletRequestAttributes) requestAttributes).getRequest()).getHeader("CLIENT_SECRET");
            if (StringUtils.isBlank(value)) {
                throw new UsernameNotFoundException("Секретный ключ отсуствует");
            } else if (!applicationProperties.getClientSecret().equals(value)) {
                throw new UsernameNotFoundException("Секретный ключ неправильный");
            }
        }
    }
}
