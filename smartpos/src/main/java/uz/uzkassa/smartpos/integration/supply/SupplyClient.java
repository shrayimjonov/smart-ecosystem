package uz.uzkassa.smartpos.integration.supply;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestClient;
import uz.uzkassa.smartpos.config.ApplicationProperties;
import uz.uzkassa.smartpos.dto.AuthResponse;
import uz.uzkassa.smartpos.dto.base.ClickhouseCommonDTO;
import uz.uzkassa.smartpos.dto.base.CommonCodeDTO;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.dto.filter.BaseFilter;
import uz.uzkassa.smartpos.dto.filter.SingleCatalogFilter;
import uz.uzkassa.smartpos.integration.cto.Credential;
import uz.uzkassa.smartpos.integration.enums.DeliveryType;
import uz.uzkassa.smartpos.integration.supply.dto.*;
import uz.uzkassa.smartpos.integration.supply.filter.CommentFilter;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Component
public final class SupplyClient {
    private final RestClient restClient;

    public SupplyClient(ApplicationProperties applicationProperties, @Qualifier("pooledIntegrationHttpClient") CloseableHttpClient httpClient) {
        restClient = RestClient.builder()
                .baseUrl(applicationProperties.getSupply().getHost())
                .requestFactory(new HttpComponentsClientHttpRequestFactory(httpClient))
                .build();
    }

    public ResponseEntity<AuthResponse> login(Credential credential) {
        return restClient.post()
                .uri("/authenticate")
                .body(credential)
                .retrieve()
                .toEntity(AuthResponse.class);
    }

    public ResponseEntity<Object> getSuppliers(String token, BaseFilter filter) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/admin/v1/suppliers")
                        .queryParams(filter.getMultiValueMap())
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve().toEntity(Object.class);
    }

    public ResponseEntity<List<DeliveryType>> getSupplierDeliveryTypes(String token, Long companyId) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/admin/v1/companies/marketplace-settings/{companyId}")
                        .build(Map.of("companyId", companyId))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve().toEntity(new ParameterizedTypeReference<>() {
                });
    }

    public ResponseEntity<List<SupplierBranchWithAddressDTO>> getBranchesWithAddress(String token, BaseFilter filter) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/admin/v1/branches/lookup/with-address")
                        .queryParams(filter.getMultiValueMap())
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve().toEntity(new ParameterizedTypeReference<>() {
                });
    }

    public ResponseEntity<Long> createOrder(String token, SupplierOrderDTO orderCreateDTO) {
        return restClient.post()
                .uri("/admin/v1/orders")
                .header(HttpHeaders.AUTHORIZATION, token)
                .body(orderCreateDTO)
                .retrieve()
                .toEntity(Long.class);
    }


    public ResponseEntity<Long> updateOrder(String token, Long id, SupplierOrderDTO orderCreateDTO) {
        return restClient.put()
                .uri(uriBuilder ->
                        uriBuilder.path("/admin/v1/orders/{id}")
                                .build(Map.of("id", id))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .body(orderCreateDTO)
                .retrieve()
                .toEntity(Long.class);
    }

    public void deleteOrder(String token, Long id) {
        restClient.delete()
                .uri(uriBuilder ->
                        uriBuilder.path("/admin/v1/orders/{id}")
                                .build(Map.of("id", id))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(Object.class);
    }


    public ResponseEntity<SupplierOrderDetailDTO> getOrderById(String token, Long id) {
        return restClient.get()
                .uri(uriBuilder ->
                        uriBuilder.path("/admin/v1/orders/{id}")
                                .build(Map.of("id", id))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(SupplierOrderDetailDTO.class);
    }


    public ResponseEntity<List<CommonDTO>> lookUpWarehouse(String token, BaseFilter filter) {
        return restClient.get()
                .uri(uriBuilder ->
                        uriBuilder.path("/admin/v1/warehouses/lookup")
                                .queryParams(filter.getMultiValueMap())
                                .build()
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });
    }

    public ResponseEntity<List<MarketplaceCategoryTreeDTO>> getCategory(String token, SingleCatalogFilter filter) {
        return restClient.get()
                .uri(uriBuilder ->
                        uriBuilder.path("/admin/v1/marketplace-categories/tree")
                                .queryParams(filter.getMultiValueMap())
                                .build()
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });
    }

    public ResponseEntity<MarketPlaceCategoryDetailDTO> getCategoryById(String token, String id) {
        return restClient.get()
                .uri(uriBuilder ->
                        uriBuilder.path("/admin/v1/marketplace-categories/{id}")
                                .build(Map.of("id", id))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(MarketPlaceCategoryDetailDTO.class);
    }

    @GetMapping("/admin/v1/marketplace")
    public ResponseEntity<Object> getMarketplaceList(String token, SingleCatalogFilter filter) {
        return restClient.get()
                .uri(uriBuilder ->
                        uriBuilder.path("/admin/v1/marketplace")
                                .queryParams(filter.getMultiValueMap())
                                .build()
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<MarketplaceProductDetailDTO> getMarketplaceListById(String token, String id) {
        return restClient.get()
                .uri(uriBuilder ->
                        uriBuilder.path("/admin/v1/marketplace/{id}")
                                .build(Map.of("id", id))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(MarketplaceProductDetailDTO.class);
    }

    public ResponseEntity<List<MarketplaceCategoryTreeDTO>> getMarketPlaceTopCategory(String token, SingleCatalogFilter filter) {
        return restClient.get()
                .uri(uriBuilder ->
                        uriBuilder.path("/admin/v1/marketplace-categories/top-categories")
                                .queryParams(filter.getMultiValueMap())
                                .build()
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });
    }

    public ResponseEntity<Object> getSuppliersList(String token, SingleCatalogFilter filter) {
        return restClient.get()
                .uri(uriBuilder ->
                        uriBuilder.path("/admin/v1/marketplace/suppliers")
                                .queryParams(filter.getMultiValueMap())
                                .build()
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(Object.class);
    }

    public void updateOrderStatus(String token, Long id, String status) {
        restClient.put()
                .uri(uriBuilder ->
                        uriBuilder.path("/admin/v1/orders/{id}/status")
                                .queryParamIfPresent("status", Optional.ofNullable(status))
                                .build(Map.of("id", id))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve();
    }


    public ResponseEntity<List<MarketplaceProductLookupDTO>> marketplaceProductLookUp(String token, BaseFilter filter) {
        return restClient.get()
                .uri(uriBuilder ->
                        uriBuilder.path("/admin/v1/marketplace/lookup")
                                .queryParams(filter.getMultiValueMap())
                                .build()
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });
    }

    public ResponseEntity<List<MarketplaceReferenceListDTO>> getMarketplaceReferenceList(String token, SingleCatalogFilter filter) {
        return restClient.get()
                .uri(uriBuilder ->
                        uriBuilder.path("/admin/v1/marketplace-references")
                                .queryParams(filter.getMultiValueMap())
                                .build()
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });
    }

    public ResponseEntity<MarketplaceReferenceDetailDTO> getMarketplaceReferenceById(String token, String id) {
        return restClient.get()
                .uri(uriBuilder ->
                        uriBuilder.path("/admin/v1/marketplace-references/{id}")
                                .build(Map.of("id", id))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(MarketplaceReferenceDetailDTO.class);
    }

    public ResponseEntity<List<ClickhouseCommonDTO>> getMarketPlaceParentById(String token, String id, boolean isProduct) {
        return restClient.get()
                .uri(uriBuilder ->
                        uriBuilder.path("/admin/v1/marketplace/parents/{id}")
                                .queryParam("isProduct", isProduct)
                                .build(Map.of("id", id))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });
    }


    public ResponseEntity<List<SupplierContractLookupDTO>> contractsLookup(String token, Long companyId, String customerTin) {
        return restClient.get()
                .uri(uriBuilder ->
                        uriBuilder.path("/admin/v1/contracts/lookup")
                                .queryParamIfPresent("companyId", Optional.ofNullable(companyId))
                                .queryParamIfPresent("customerTin", Optional.ofNullable(customerTin))
                                .build()
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });
    }

    public ResponseEntity<ContractDetailDTO> contractGetById(String token, Long id) {
        return restClient.get()
                .uri(uriBuilder ->
                        uriBuilder.path("/admin/v1/contracts/{id}")
                                .build(Map.of("id", id))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(ContractDetailDTO.class);
    }

    @GetMapping("/public/v1/regions/items")
    public List<CommonCodeDTO> regions() {
        return restClient.get()
                .uri(uriBuilder ->
                        uriBuilder.path("/public/v1/regions/items")
                                .build()
                )
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public List<CommonCodeDTO> districts(Long regionId) {
        return restClient.get()
                .uri(uriBuilder ->
                        uriBuilder.path("/public/v1/districts/items/{regionId}")
                                .build(Map.of("regionId", regionId))
                )
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public ResponseEntity<Long> createComment(String token, SupplierCommentDTO commentDTO) {
        return restClient.post()
                .uri("/admin/v1/comments")
                .header(HttpHeaders.AUTHORIZATION, token)
                .body(commentDTO)
                .retrieve()
                .toEntity(Long.class);
    }


    public ResponseEntity<Object> getComments(String token, CommentFilter filter) {
        return restClient.get()
                .uri(uriBuilder ->
                        uriBuilder.path("/admin/v1/comments")
                                .queryParams(filter.getMultiValueMap())
                                .build()
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(Object.class);
    }

    public String getToken(String username, String password) {
        ResponseEntity<AuthResponse> responseEntity = login(new Credential(username, password));
        return Objects.requireNonNull(responseEntity.getBody()).getIdToken();
    }
}
