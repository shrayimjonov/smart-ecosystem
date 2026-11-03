package uz.uzkassa.smartpos.web.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpos.constants.TokenType;
import uz.uzkassa.smartpos.dto.base.ClickhouseCommonDTO;
import uz.uzkassa.smartpos.dto.filter.SingleCatalogFilter;
import uz.uzkassa.smartpos.integration.supply.SupplyClient;
import uz.uzkassa.smartpos.integration.supply.dto.MarketPlaceCategoryDetailDTO;
import uz.uzkassa.smartpos.integration.supply.dto.MarketplaceCategoryTreeDTO;
import uz.uzkassa.smartpos.service.TokenStoreService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierMarketplaceCategoryResource {
    private final SupplyClient supplyClient;
    private final TokenStoreService tokenStoreService;

    @GetMapping("/marketplace-categories/tree")
    public ResponseEntity<List<MarketplaceCategoryTreeDTO>> getCategory(@ParameterObject SingleCatalogFilter filter) {
        return supplyClient.getCategory(tokenStoreService.getToken(TokenType.SUPPLY), filter);
    }

    @GetMapping("/top-category")
    public ResponseEntity<List<MarketplaceCategoryTreeDTO>> getMarketPlaceTopCategory(@ParameterObject SingleCatalogFilter filter) {
        return supplyClient.getMarketPlaceTopCategory(tokenStoreService.getToken(TokenType.SUPPLY), filter);
    }

    @GetMapping("/marketplace-categories/{id}")
    public ResponseEntity<MarketPlaceCategoryDetailDTO> getCategoryById(@PathVariable String id) {
        return supplyClient.getCategoryById(tokenStoreService.getToken(TokenType.SUPPLY), id);
    }


    @GetMapping("/parents" + "/{id}")
    public ResponseEntity<List<ClickhouseCommonDTO>> getParents(@PathVariable(value = "id") String id, boolean isProduct) {
        return supplyClient.getMarketPlaceParentById(tokenStoreService.getToken(TokenType.SUPPLY), id, isProduct);
    }
}
