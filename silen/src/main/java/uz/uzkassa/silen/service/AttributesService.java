package uz.uzkassa.silen.service;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.silen.domain.Attributes;
import uz.uzkassa.silen.domain.ProductAttributes;
import uz.uzkassa.silen.dto.AttributesDTO;
import uz.uzkassa.silen.dto.AttributesListDTO;
import uz.uzkassa.silen.dto.ProductAttributesDTO;
import uz.uzkassa.silen.dto.filter.AttributesFilter;
import uz.uzkassa.silen.repository.pg.AttributesRepository;
import uz.uzkassa.silen.repository.pg.ProductAttributesRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AttributesService extends BaseService {
    AttributesRepository attributesRepository;
    ProductAttributesRepository productAttributesRepository;

    @Transactional
    public AttributesListDTO save(AttributesDTO dto) {
        attributesRepository.getByNameAndPrefixAndPostfixAndProductTypesIn(dto.getName(), dto.getPrefix(), dto.getPostfix(), dto.getProductTypes())
            .ifPresent(attributes -> {
                throw badRequest("Атрибут уже существует").get();
            });
        attributesRepository.getAttributesBySorter(dto.getSorter())
            .ifPresent(attributes -> {
                throw badRequest("С таким порядочность номером уже существует").get();
            });
        if (dto.getSorter() == 0) {
            int sortOrderValue = attributesRepository.getMaxSortOrderValue() + 1;
            dto.setSorter(sortOrderValue);
        }
        Attributes entity = new Attributes();
        entity.setName(dto.getName());
        entity.setPrefix(dto.getPrefix());
        entity.setPostfix(dto.getPostfix());
        entity.setParentId(dto.getParentId());
        entity.setAdditional(dto.getAdditional());
        entity.setProductTypes(dto.getProductTypes());
        entity.setVisible(dto.isVisible());
        entity.setSorter(dto.getSorter());
        entity = attributesRepository.save(entity);
        return entity.toDto();
    }

    public AttributesListDTO findOne(Long id) {
        Attributes attribute = attributesRepository.findById(id).orElseThrow(badRequest("Атрибут не найден"));
        return attribute.toDto();
    }

    public List<AttributesListDTO> getItemsList(AttributesFilter productType) {
        return attributesRepository.findAllByProductTypesContainsOrderByLastModifiedDate(productType.getProductType())
            .stream().map(Attributes::toDto).collect(Collectors.toList());
    }

    public Page<AttributesListDTO> findAll(AttributesFilter filter) {
        return attributesRepository.findAllByFilter(filter)
            .map(Attributes::toDto);
    }

    public List<ProductAttributesDTO> findAllByProduct(String productId) {
        return productAttributesRepository.findAllByProductId(productId)
            .stream().map(ProductAttributes::toDto)
            .collect(Collectors.toList());
    }

    @Transactional
    public void update(AttributesListDTO dto) {
        Attributes entity = attributesRepository.findById(dto.getId()).orElseThrow(badRequest("Атрибут не найден"));

        attributesRepository.getAttributesBySorter(dto.getSorter()).ifPresent(attributes -> {
            if (!Objects.equals(attributes.getId(), dto.getId())) {
                throw badRequest("С таким порядочность номером уже существует").get();
            }
        });
        attributesRepository.getByNameAndPrefixAndPostfixAndProductTypesIn(dto.getName(), dto.getPrefix(), dto.getPostfix(), dto.getProductTypes())
            .ifPresent(attributes -> {
                if (!Objects.equals(attributes.getId(), dto.getId())) {
                    throw badRequest("Атрибут уже существует").get();
                }
            });

        entity.setName(dto.getName());
        entity.setPrefix(dto.getPrefix());
        entity.setPostfix(dto.getPostfix());
        entity.setParentId(dto.getParentId());
        entity.setAdditional(dto.getAdditional());
        entity.setProductTypes(dto.getProductTypes());
        entity.setVisible(dto.isVisible());
//        entity.setSorter(dto.getSorter());
        attributesRepository.save(entity);
    }

    @Transactional
    public void delete(Long id) {
        Attributes entity = attributesRepository.findById(id).orElseThrow(badRequest("Атрибут не найден"));
        attributesRepository.delete(entity);
    }
}
