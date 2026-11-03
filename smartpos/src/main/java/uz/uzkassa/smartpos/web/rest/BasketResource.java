package uz.uzkassa.smartpos.web.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.BasketDTO;
import uz.uzkassa.smartpos.dto.BasketItemDTO;
import uz.uzkassa.smartpos.dto.BasketItemUpdateDTO;
import uz.uzkassa.smartpos.service.BasketService;

import java.util.List;

import static uz.uzkassa.smartpos.security.AuthoritiesConstants.*;

@RestController
@RequestMapping("/api/basket")
@Secured({SMARTPOS_ADMIN, SALES_MANAGER, OWNER, SUPPORT, ADMIN, SUPPORT_MANAGER})
@RequiredArgsConstructor
@Slf4j
public class BasketResource {
    private final BasketService basketService;

    @PostMapping("/products")
    public BasketItemDTO addProduct(@RequestBody @Valid BasketItemDTO basketDTO) {
        return basketService.addProduct(basketDTO);
    }

    @PutMapping("/products")
    public BasketItemDTO updateProduct(@RequestBody @Valid BasketItemUpdateDTO basketDTO) {
        return basketService.updateProduct(basketDTO);
    }

    @DeleteMapping("/products/{supplierProductId}")
    public ResponseEntity<Void> removeProduct(@PathVariable Long supplierProductId, @RequestParam(required = false) Long companyId) {
        basketService.removeProduct(supplierProductId, companyId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public List<BasketDTO> getBasket(@RequestParam(required = false) Long companyId, @RequestParam(required = false) Long supplyId) {
        return basketService.getBasket(companyId, supplyId);
    }

    @DeleteMapping
    public ResponseEntity<Void> removeBasket(@RequestParam(required = false) Long companyId) {
        basketService.removeBasket(companyId);
        return ResponseEntity.ok().build();
    }

}
