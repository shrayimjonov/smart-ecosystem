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
import uz.uzkassa.smartpos.dto.filter.SingleCatalogFilter;
import uz.uzkassa.smartpos.integration.supply.SupplyClient;
import uz.uzkassa.smartpos.integration.supply.dto.MarketplaceReferenceDetailDTO;
import uz.uzkassa.smartpos.integration.supply.dto.MarketplaceReferenceListDTO;
import uz.uzkassa.smartpos.service.TokenStoreService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/suppliers/marketplace-references")
@RequiredArgsConstructor
public class SupplierMarketplaceReferenceResource {
    private final SupplyClient supplyClient;
    private final TokenStoreService tokenStoreService;

    @GetMapping()
    public ResponseEntity<List<MarketplaceReferenceListDTO>> getReferenceList(@ParameterObject SingleCatalogFilter filter) {
        return supplyClient.getMarketplaceReferenceList(tokenStoreService.getToken(TokenType.SUPPLY), filter);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarketplaceReferenceDetailDTO> getReferenceById(@PathVariable(value = "id") String id) {
        return supplyClient.getMarketplaceReferenceById(tokenStoreService.getToken(TokenType.SUPPLY), id);
    }
}
