package uz.uzkassa.smartpos.web.rest.fashion;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.fashion.FashionStockAdjustmentItemDto;
import uz.uzkassa.smartpos.dto.warehouse.fashion.FashionStockAdjustmentCreateDto;
import uz.uzkassa.smartpos.enums.StockAdjustmentReason;
import uz.uzkassa.smartpos.filter.FashionRegisterFilter;
import uz.uzkassa.smartpos.service.fashion.FashionStockAdjustmentService;

import java.util.EnumSet;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/fashion/cabinet/stock-adjustment")
public class FashionStockAdjustmentResource {
    private final FashionStockAdjustmentService stockAdjustmentService;

    @PostMapping
    public void create(@RequestBody @Valid FashionStockAdjustmentCreateDto dto) {
        stockAdjustmentService.create(dto);
    }

    @GetMapping
    public Page<FashionStockAdjustmentItemDto> findAllByFilter(@ParameterObject FashionRegisterFilter filter) {
        return stockAdjustmentService.findAllByFilter(filter);
    }

    @GetMapping("/reasons")
    public EnumSet<StockAdjustmentReason> getReasons() {
        return EnumSet.complementOf(EnumSet.of(StockAdjustmentReason.EXPIRED));
    }
}
