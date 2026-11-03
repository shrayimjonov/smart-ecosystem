package uz.uzkassa.smartpos.web.rest.fashion;

import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpos.dto.analytics.RegisterStatsDTO;
import uz.uzkassa.smartpos.dto.fashion.ColorGroupItemsRegisterDto;
import uz.uzkassa.smartpos.dto.fashion.FashionBranchColorGroupRegisterDto;
import uz.uzkassa.smartpos.dto.warehouse.fashion.FashionColorGroupRegisterDto;
import uz.uzkassa.smartpos.dto.warehouse.fashion.FashionRegisterDto;
import uz.uzkassa.smartpos.filter.FashionRegisterFilter;
import uz.uzkassa.smartpos.service.fashion.FashionRegisterService;
import uz.uzkassa.smartpos.utils.ServerUtils;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fashion/cabinet/register")
public class FashionRegisterResource {
    private final FashionRegisterService fashionRegisterService;

    @GetMapping("/stats")
    public RegisterStatsDTO getStats(@ParameterObject FashionRegisterFilter filter) {
        return fashionRegisterService.getStats(filter);
    }

    @GetMapping
    public Page<FashionRegisterDto> findAll(@ParameterObject FashionRegisterFilter filter) {
        return fashionRegisterService.findAllByFilter(filter);
    }

    @GetMapping("/excel")
    public ResponseEntity<Resource> findAllToExcel(@ParameterObject FashionRegisterFilter filter) {
        return ServerUtils.getResponseEntity(fashionRegisterService.findAllToExcel(filter), "Общие остатки");
    }

    @GetMapping("/color-group")
    public List<FashionColorGroupRegisterDto> colorGroupRegisters(@ParameterObject FashionRegisterFilter filter) {
        return fashionRegisterService.colorGroupRegisters(filter);
    }

    @GetMapping("/branches")
    public Page<FashionBranchColorGroupRegisterDto> branchColorGroupRegisters(@ParameterObject FashionRegisterFilter filter) {
        return fashionRegisterService.branchColorGroupRegisters(filter);
    }

    @GetMapping("/items")
    public List<ColorGroupItemsRegisterDto> colorGroupItemsRegisters(@ParameterObject FashionRegisterFilter filter) {
        return fashionRegisterService.colorGroupItemsRegisters(filter);
    }

    @GetMapping("/items/amount")
    public BigDecimal getQty(@RequestParam Long branchId, @RequestParam Long productItemId) {
        return fashionRegisterService.getQty(branchId, productItemId);
    }

    @GetMapping("/products/amount")
    public BigDecimal getQtyByProduct(@RequestParam Long branchId, @RequestParam Long productId) {
        return fashionRegisterService.getQtyByProduct(branchId, productId);
    }
}
