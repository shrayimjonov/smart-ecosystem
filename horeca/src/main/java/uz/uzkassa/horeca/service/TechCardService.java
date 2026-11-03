package uz.uzkassa.horeca.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.horeca.domain.base.SimpleEntity;
import uz.uzkassa.horeca.domain.product.Product;
import uz.uzkassa.horeca.domain.techcard.TechCard;
import uz.uzkassa.horeca.domain.techcard.TechCardItem;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.dto.techcard.*;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.interfaces.Crud;
import uz.uzkassa.horeca.interfaces.Validatable;
import uz.uzkassa.horeca.repository.MenuItemRepository;
import uz.uzkassa.horeca.repository.ProductRepository;
import uz.uzkassa.horeca.repository.TechCardItemRepository;
import uz.uzkassa.horeca.repository.TechCardRepository;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class TechCardService
        extends BaseService
        implements Crud<Long, TechCardDTO, TechCardDetailDTO, TechCardListDTO, BaseFilter>, Validatable<TechCardDTO> {

    TechCardRepository techCardRepository;
    TechCardItemRepository techCardItemRepository;
    ProductRepository productRepository;
    MenuItemRepository menuItemRepository;

    @Override
    public void validate(TechCardDTO techCardDTO) {
        if (techCardDTO.getProductId() == null) {
            throw badRequestExceptionThrow("Product is required").get();
        }
        if (techCardDTO.getUnitId() == null) {
            throw badRequestExceptionThrow("Unit is required").get();
        }
        if (techCardDTO.getSize() == null) {
            throw badRequestExceptionThrow("Size is required").get();
        }
        if (techCardDTO.getId() == null && techCardRepository.existsByProductIdAndSizeAndUnitIdAndDeletedIsFalse(techCardDTO.getProductId(), techCardDTO.getSize(), techCardDTO.getUnitId())) {
            throw conflictExceptionThrow("Tech card exists by this product").get();
        }
        if (
                techCardDTO.getId() != null &&
                        techCardRepository.existsByProductIdAndSizeAndUnitIdAndDeletedIsFalseAndIdNot(techCardDTO.getProductId(), techCardDTO.getSize(), techCardDTO.getUnitId(), techCardDTO.getId())
        ) {
            throw conflictExceptionThrow("Tech card exists by this product").get();
        }
        if (CollectionUtils.isEmpty(techCardDTO.getItems())) {
            throw badRequestExceptionThrow("Items are required").get();
        }
    }

    private void validateItems(TechCardItemDTO techCardItemDTO) {
        if (techCardItemDTO.getIngredientId() == null) {
            throw badRequestExceptionThrow("IngredientId is required").get();
        }
        if (techCardItemDTO.getGross() == null) {
            throw badRequestExceptionThrow("Gross is required").get();
        }
    }

    @Transactional
    public Long create(TechCardDTO techCardDTO) {
        this.validate(techCardDTO);
        TechCard techCard = new TechCard();
        techCard.setProductId(techCardDTO.getProductId());
        techCard.setDescription(techCardDTO.getDescription());
        techCard.setPrice(techCardDTO.getPrice());
        techCard.setUnitId(techCardDTO.getUnitId());
        techCard.setSize(techCardDTO.getSize());
        techCardRepository.save(techCard);
        techCardDTO
                .getItems()
                .forEach(techCardItemDTO -> {
                    this.validateItems(techCardItemDTO);
                    TechCardItem techCardItem = new TechCardItem();
                    techCardItem.setTechCardId(techCard.getId());
                    techCardItem.setTechCard(techCard);
                    techCardItem.setIngredientId(techCardItemDTO.getIngredientId());
                    techCardItem.setCookingTypes(techCardItemDTO.getCookingTypes());
                    techCardItem.setGross(techCardItemDTO.getGross());
                    techCardItem.setNet(techCardItemDTO.getNet());
                    techCardItem.setUnitId(techCardItemDTO.getUnitId());
                    techCardItem.setInKg(techCardItemDTO.getInKg());
                    techCard.addItem(techCardItem);
                });
        productRepository.findById(techCardDTO.getProductId()).ifPresent(product -> {
            if (CommonStatus.IN_ACTIVE == product.getStatus()) {
                product.setStatus(CommonStatus.ACTIVE);
                productRepository.save(product);
            }
        });
        return techCard.getId();
    }

    @Transactional
    public void batchCreate(List<TechCardDTO> techCardDTOList) {
        techCardDTOList.forEach(this::create);
    }

    @Override
    @Transactional
    public Long update(Long id, TechCardDTO techCardDTO) {
        return techCardRepository.findById(id)
                .map(techCard -> {
                    techCardDTO.setId(id);
                    this.validate(techCardDTO);
                    techCard.setProductId(techCardDTO.getProductId());
                    techCard.setDescription(techCardDTO.getDescription());
                    techCard.setPrice(techCardDTO.getPrice());
                    techCard.setUnitId(techCardDTO.getUnitId());
                    techCard.setSize(techCardDTO.getSize());

                    HashMap<Long, TechCardItem> itemsMap = new HashMap<>();
                    techCard.getItems().forEach(techCardItem -> itemsMap.put(techCardItem.getId(), techCardItem));

                    techCardDTO
                            .getItems()
                            .forEach(techCardItemDTO -> {
                                this.validateItems(techCardItemDTO);
                                TechCardItem techCardItem;
                                if (techCardItemDTO.getId() != null && itemsMap.get(techCardItemDTO.getId()) != null) {
                                    techCardItem = itemsMap.get(techCardItemDTO.getId());
                                } else {
                                    techCardItem = new TechCardItem();
                                }
                                techCardItem.setIngredientId(techCardItemDTO.getIngredientId());
                                if (!CollectionUtils.isEmpty(techCardItem.getCookingTypes())) {
                                    techCardItem.getCookingTypes().clear();
                                }
                                if (!CollectionUtils.isEmpty(techCardItemDTO.getCookingTypes())) {
                                    techCardItem.setCookingTypes(techCardItemDTO.getCookingTypes());
                                }
                                techCardItem.setGross(techCardItemDTO.getGross());
                                techCardItem.setNet(techCardItemDTO.getNet());
                                techCardItem.setUnitId(techCardItemDTO.getUnitId());
                                techCardItem.setInKg(techCardItemDTO.getInKg());
                                techCard.addItem(techCardItem);
                                if (techCardItem.getId() != null) {
                                    itemsMap.remove(techCardItem.getId());
                                }
                            });
                    techCardRepository.save(techCard);
                    if (!itemsMap.isEmpty()) {
                        techCardItemRepository.deleteAllInBatch(itemsMap.values());
                    }
                    return id;
                }).orElseThrow(notFoundExceptionThrow(TechCard.class.getSimpleName(), "id", id));
    }

    @Transactional
    public void batchUpdate(List<TechCardDTO> techCardDTOList) {
        techCardDTOList.forEach(techCardDTO -> this.update(techCardDTO.getId(), techCardDTO));
    }

    @Override
    public TechCardDetailDTO get(Long id) {
        return techCardRepository
                .findById(id)
                .map(this::getDetails)
                .orElseThrow(notFoundExceptionThrow(TechCard.class.getSimpleName(), "id", id));
    }

    public List<TechCardDetailDTO> getListByProductId(Long productId) {
        return techCardRepository.findAllByProductIdAndDeletedIsFalse(productId)
                .stream()
                .map(this::getDetails)
                .collect(Collectors.toList());
    }

    public TechCardDetailDTO getDetails(TechCard techCard) {
        TechCardDetailDTO techCardDetailDTO = new TechCardDetailDTO();
        techCardDetailDTO.setId(techCard.getId());
        techCardDetailDTO.setDescription(techCard.getDescription());
        techCardDetailDTO.setProduct(techCard.getProduct().toCommonDTO());
        techCardDetailDTO.setPrice(techCard.getPrice());
        techCardDetailDTO.setSize(techCard.getSize());
        techCardDetailDTO.setUnit(techCard.getUnit().toCommonCodeDTO());
        techCardDetailDTO.setItems(
                techCard
                        .getItems()
                        .stream()
                        .map(techCardItem -> {
                            TechCardItemDetailDTO techCardItemDetailDTO = new TechCardItemDetailDTO();
                            techCardItemDetailDTO.setId(techCardItem.getId());
                            techCardItemDetailDTO.setIngredient(techCardItem.getIngredient().toCommonDTO());
                            techCardItemDetailDTO.setCookingTypes(techCardItem.getCookingTypes());
                            techCardItemDetailDTO.setGross(techCardItem.getGross());
                            techCardItemDetailDTO.setNet(techCardItem.getNet());
                            techCardItemDetailDTO.setInKg(techCardItem.getInKg());
                            if (techCardItem.getUnitId() != null) {
                                techCardItemDetailDTO.setUnit(techCardItem.getUnit().toCommonDTO());
                            }
                            return techCardItemDetailDTO;
                        })
                        .collect(Collectors.toList())
        );
        return techCardDetailDTO;
    }

    @Override
    public Page<TechCardListDTO> getList(BaseFilter filter) {
        ResultList<TechCard> resultList = techCardRepository.getResultList(filter);
        List<TechCardListDTO> result = resultList
                .getList()
                .stream()
                .map(techCard -> {
                    TechCardListDTO productListDTO = new TechCardListDTO();
                    productListDTO.setId(techCard.getId());
                    productListDTO.setProduct(techCard.getProduct().toCommonDTO());
                    productListDTO.setDescription(techCard.getDescription());
                    productListDTO.setPrice(techCard.getPrice());
                    return productListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public List<CommonDTO> getItems(Long productId) {
        return techCardRepository.findAllByProductIdAndDeletedIsFalse(productId)
                .stream()
                .map(SimpleEntity::toCommonDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        TechCard techCard = techCardRepository.findById(id).orElseThrow(notFoundExceptionThrow(TechCard.class.getSimpleName(), "id", id));
        Product product = techCard.getProduct();
        techCardRepository.delete(techCard);
        if (CommonStatus.ACTIVE == product.getStatus()) {
            List<TechCard> techCards = techCardRepository.findAllByProductIdAndDeletedIsFalse(product.getId());
            if (techCards.isEmpty()) {
                menuItemRepository.findAllByProductIdAndStatus(product.getId(), CommonStatus.ACTIVE)
                        .forEach(menuItem -> {
                            menuItem.setStatus(CommonStatus.IN_ACTIVE);
                            menuItemRepository.save(menuItem);
                        });
                product.setStatus(CommonStatus.IN_ACTIVE);
                productRepository.save(product);
            }
        }
    }
}
