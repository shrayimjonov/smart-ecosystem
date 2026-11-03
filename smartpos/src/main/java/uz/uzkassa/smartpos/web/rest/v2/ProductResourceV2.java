package uz.uzkassa.smartpos.web.rest.v2;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.EnumDTO;
import uz.uzkassa.smartpos.dto.catalog.CreditSettingsDto;
import uz.uzkassa.smartpos.dto.catalog.ProductDTO;
import uz.uzkassa.smartpos.dto.catalog.ProductReceiptDTO;
import uz.uzkassa.smartpos.dto.catalog.ProductStatisticsDTO;
import uz.uzkassa.smartpos.dto.filter.ProductFilter;
import uz.uzkassa.smartpos.dto.markings.MarkingRequestDTO;
import uz.uzkassa.smartpos.dto.receipt.ScheduleDTO;
import uz.uzkassa.smartpos.dto.receipt.ScheduleRequestDTO;
import uz.uzkassa.smartpos.enums.ProductOrigin;
import uz.uzkassa.smartpos.service.MarkingService;
import uz.uzkassa.smartpos.service.ProductService;
import uz.uzkassa.smartpos.service.SchedulerService;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class ProductResourceV2 {
    private final ProductService productService;
    private final MarkingService markingService;
    private final SchedulerService schedulerService;

    @GetMapping("/products/page")
    public Page<ProductDTO> getProductPage(@ParameterObject ProductFilter filter) {
        return productService.getPageV2(filter);
    }

    @GetMapping("/products/credit/page")
    public Page<ProductDTO> getCreditProductPage(@ParameterObject ProductFilter filter) {
        if (!filter.isCreditEnabled() && !filter.isAdvanceEnabled()) {
            filter.setCreditEnabled(true);
            filter.setAdvanceEnabled(true);
        }
        return productService.getPageV2(filter);
    }

    @GetMapping("/products/statistics")
    public ProductStatisticsDTO getProductStatistics(@ParameterObject ProductFilter filter) {
        return productService.getProductStatistics(filter);
    }

    @PutMapping("/products/settings")
    public void updateSettings(@RequestBody CreditSettingsDto dto) {
        productService.updateSettings(dto);
    }

    @PostMapping("/products/marking/check")
    public HashMap<String, Set<String>> isValidMarking(@RequestBody @Valid MarkingRequestDTO dto) {
        return markingService.checkCode(dto);
    }

    @PostMapping("/products/marking")
    public ProductReceiptDTO findByMarking(@RequestBody @Valid MarkingRequestDTO dto) {
        return markingService.findProductByMarking(dto);
    }

    @PostMapping("/products/receipts/schedule")
    public ScheduleDTO scheduleBillsForProduct(@RequestBody ScheduleRequestDTO dto) {
        return schedulerService.schedule(dto, false);
    }

    @GetMapping("/products/origins")
    public List<EnumDTO> getAllProductOrigins() {
        return ProductOrigin.getAll();
    }
}
