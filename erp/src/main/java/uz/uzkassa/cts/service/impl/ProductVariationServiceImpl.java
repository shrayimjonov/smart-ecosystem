package uz.uzkassa.cts.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.cts.domain.inventory.ProductVariation;
import uz.uzkassa.cts.domain.inventory.VariationOption;
import uz.uzkassa.cts.domain.inventory.VariationType;
import uz.uzkassa.cts.dto.productvariation.ProductVariationDTO;
import uz.uzkassa.cts.dto.productvariation.admin.ProductVariationListByProductAdminDTO;
import uz.uzkassa.cts.dto.productvariation.admin.VariationTypeItemDTO;
import uz.uzkassa.cts.repository.ProductVariationRepository;
import uz.uzkassa.cts.service.ProductVariationService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class ProductVariationServiceImpl extends BaseServiceImpl<ProductVariation, Long> implements ProductVariationService {

    private final ProductVariationRepository productVariationRepository;
    private final ValidationContext validationContext;

    public ProductVariationServiceImpl(ProductVariationRepository productVariationRepository, ValidationContext validationContext) {
        super(productVariationRepository);
        this.productVariationRepository = productVariationRepository;
        this.validationContext = validationContext;
    }

    @Override
    @Transactional
    public void create(ProductVariationDTO productVariationDTO) {
        validationContext.validate(productVariationDTO);
        Map<Long, ProductVariation> productVariationMap = productVariationRepository.findAllByProductIdAndDeletedIsFalse(productVariationDTO.getProductId())
                .stream()
                .collect(Collectors.toMap(ProductVariation::getId, Function.identity()));

        List<ProductVariation> productVariations = productVariationDTO.getVariationOptionIds().stream().map(variationOptionId -> {
            ProductVariation productVariation = productVariationRepository.findFirstByProductIdAndVariationOptionIdAndDeletedIsFalse(productVariationDTO.getProductId(), variationOptionId).orElseGet(ProductVariation::new);
            productVariation.setProductId(productVariationDTO.getProductId());
            productVariation.setVariationOptionId(variationOptionId);
            if (productVariation.getId() != null) {
                productVariationMap.remove(productVariation.getId());
            }
            return productVariation;
        }).collect(Collectors.toList());
        productVariationRepository.saveAll(productVariations);
        if (!CollectionUtils.isEmpty(productVariationMap)) {
            productVariationRepository.deleteAllInBatch(productVariationMap.values());
        }
    }

    @Override
    public ProductVariationListByProductAdminDTO getListByProductId(Long productId) {
        List<ProductVariation> productVariations = productVariationRepository.findAllByProductIdAndDeletedIsFalse(productId);
        if (CollectionUtils.isEmpty(productVariations)) {
            return new ProductVariationListByProductAdminDTO();
        }
        ProductVariationListByProductAdminDTO productVariationListByProductDTO = new ProductVariationListByProductAdminDTO();
        productVariationListByProductDTO.setProduct(productVariations.get(0).getProduct().toCommonDTO());
        Map<VariationType, List<VariationOption>> variationTypesMap = new HashMap<>();
        productVariations.forEach(productVariation -> {
            if (variationTypesMap.get(productVariation.getVariationOption().getVariationType()) == null) {
                List<VariationOption> options = new ArrayList<>();
                options.add(productVariation.getVariationOption());
                variationTypesMap.put(productVariation.getVariationOption().getVariationType(), options);
            } else {
                variationTypesMap.get(productVariation.getVariationOption().getVariationType()).add(productVariation.getVariationOption());
            }
        });

        variationTypesMap.forEach((variationType, variationOptions) -> {
            VariationTypeItemDTO variationTypeItemDTO = new VariationTypeItemDTO();
            variationTypeItemDTO.setId(variationType.getId());
            variationTypeItemDTO.setName(variationType.getName());
            variationTypeItemDTO.setType(variationType.getType());
            variationOptions.forEach(variationOption -> variationTypeItemDTO.addValue(variationOption.toCommonDTO()));
            productVariationListByProductDTO.addField(variationTypeItemDTO);
        });

        return productVariationListByProductDTO;
    }
}
