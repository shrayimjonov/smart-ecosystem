package uz.uzkassa.horeca.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.horeca.domain.TasnifBasketProduct;
import uz.uzkassa.horeca.domain.company.Company;
import uz.uzkassa.horeca.dto.category.CompanyCatalogListDTO;
import uz.uzkassa.horeca.dto.payload.TasnifBasketProductPayload;
import uz.uzkassa.horeca.dto.tasnif.TasnifBasketProductResponseListDTO;
import uz.uzkassa.horeca.integration.TasnifClient;
import uz.uzkassa.horeca.repository.TasnifBasketProductRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class TasnifBasketProductService extends BaseService {

    TasnifBasketProductRepository tasnifBasketProductRepository;
    TasnifClient tasnifClient;

    @Transactional
    public void syncQueue(TasnifBasketProductPayload tasnifBasketProductPayload) {
        this.sync(tasnifBasketProductPayload.getId(), tasnifBasketProductPayload.getTin());
    }

    @Transactional
    public void sync(Long companyId) {
        Company company = companyId == null ? getCurrentCompany() : companyRepository.findById(companyId).orElseThrow(notFoundExceptionThrow(Company.class.getSimpleName(), "id", companyId));
        this.sync(company.getId(), company.getTin());
    }

    @Transactional
    public void sync(Long companyId, String tin) {
        TasnifBasketProductResponseListDTO tasnifBasketProductResponseDTO = tasnifClient.getCatalogByTin(tin);
        TasnifBasketProduct tasnifBasketProduct = tasnifBasketProductRepository
            .findFirstByCompanyId(companyId)
            .orElse(new TasnifBasketProduct());
        tasnifBasketProduct.setProducts(tasnifBasketProductResponseDTO.getData());
        tasnifBasketProduct.setCompanyId(companyId);
        tasnifBasketProductRepository.save(tasnifBasketProduct);
    }

    public List<CompanyCatalogListDTO> getTasnifCatalog(Long companyId) {
        return tasnifBasketProductRepository
                .findFirstByCompanyId(Optional.ofNullable(companyId).orElseGet(BaseService::getCurrentCompanyId))
                .map(tasnifBasketProduct ->
                        tasnifBasketProduct
                                .getProducts()
                                .stream()
                                .map(mxikDTO ->
                                        CompanyCatalogListDTO
                                                .builder()
                                                .id(mxikDTO.getPkey())
                                                .catalogCode(mxikDTO.getMxikCode())
                                                .catalogName(mxikDTO.getName())
                                                .barcode(mxikDTO.getInternationalCode())
                                                .packageCode(mxikDTO.getPackageNames().getFirst().getCode())
                                                .packageName(mxikDTO.getPackageNames().getFirst().getName())
                                                .build()
                                )
                                .collect(Collectors.toList())
                )
                .orElseThrow(notFoundExceptionThrow(Company.class.getSimpleName(), "id", getCurrentCompanyId()));
    }
}
