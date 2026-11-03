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
import uz.uzkassa.smartpos.dto.filter.BaseFilter;
import uz.uzkassa.smartpos.dto.filter.SingleCatalogFilter;
import uz.uzkassa.smartpos.integration.supply.SupplyClient;
import uz.uzkassa.smartpos.integration.supply.dto.MarketplaceProductDetailDTO;
import uz.uzkassa.smartpos.integration.supply.dto.MarketplaceProductLookupDTO;
import uz.uzkassa.smartpos.service.TokenStoreService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierMarketplaceResource {
    private final SupplyClient supplyClient;
    private final TokenStoreService tokenStoreService;

    @GetMapping("/marketplace")
    public ResponseEntity<Object> getMarketplaceList(@ParameterObject SingleCatalogFilter filter) {
        return supplyClient.getMarketplaceList(tokenStoreService.getToken(TokenType.SUPPLY), filter);
    }

    @GetMapping("/marketplace/{id}")
    public ResponseEntity<MarketplaceProductDetailDTO> getMarketplaceListById(@PathVariable String id) {
        return supplyClient.getMarketplaceListById(tokenStoreService.getToken(TokenType.SUPPLY), id);
    }

    @GetMapping("/marketplace/suppliers")
    public ResponseEntity<Object> getSuppliers(@ParameterObject SingleCatalogFilter filter) {
        return supplyClient.getSuppliersList(tokenStoreService.getToken(TokenType.SUPPLY), filter);
    }

    @GetMapping("/marketplace/product/lookUp")
    public ResponseEntity<List<MarketplaceProductLookupDTO>> getMarketplaceProductLookUp(@ParameterObject BaseFilter filter) {
        return supplyClient.marketplaceProductLookUp(tokenStoreService.getToken(TokenType.SUPPLY), filter);
    }
}
