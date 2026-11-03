package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.container.Container;
import uz.uzkassa.smartpossupply.domain.container.ContainerItem;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.container.ContainerDTO;
import uz.uzkassa.smartpossupply.dto.container.ContainerDetailDTO;
import uz.uzkassa.smartpossupply.dto.container.ContainerItemDetailDTO;
import uz.uzkassa.smartpossupply.dto.container.ContainerListDTO;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.repository.ContainerRepository;
import uz.uzkassa.smartpossupply.validator.ValidationContext;

import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ContainerService extends BaseService {

    ContainerRepository containerRepository;
    ValidationContext validationContext;

    @Transactional
    public Long create(ContainerDTO containerDTO) {
        validationContext.validate(containerDTO);
        Container container = new Container();
        container.setIdentificationNumber(containerDTO.getIdentificationNumber());
        container.setCompanyId(Optional.ofNullable(containerDTO.getCompanyId()).orElse(getCurrentCompanyId()));
        container.setType(containerDTO.getType());
        container.setWidth(containerDTO.getWidth());
        container.setHeight(containerDTO.getHeight());
        container.setDepth(containerDTO.getDepth());
        container.setDiameter(containerDTO.getDiameter());
        container.setCapacity(containerDTO.getCapacity());
        container.setMaterial(containerDTO.getMaterial());
        container.setWeight(containerDTO.getWeight());
        container.setSpecialTag(containerDTO.getSpecialTag());
        container.setComment(containerDTO.getComment());
        containerRepository.saveAndFlush(container);
        containerDTO.getItems().forEach(containerItemDTO -> {
            ContainerItem containerItem = new ContainerItem();
            containerItem.setQty(containerItemDTO.getQty());
            containerItem.setProductId(containerItemDTO.getProductId());
            container.addItem(containerItem);
        });
        return container.getId();
    }

    @Transactional
    public Long update(Long id, ContainerDTO containerDTO) {
        return containerRepository.findById(id).map(container -> {
            containerDTO.setId(id);
            validationContext.validate(containerDTO);
            container.setType(containerDTO.getType());
            container.setWidth(containerDTO.getWidth());
            container.setHeight(containerDTO.getHeight());
            container.setDepth(containerDTO.getDepth());
            container.setDiameter(containerDTO.getDiameter());
            container.setCapacity(containerDTO.getCapacity());
            container.setMaterial(containerDTO.getMaterial());
            container.setWeight(containerDTO.getWeight());
            container.setSpecialTag(containerDTO.getSpecialTag());
            container.setComment(containerDTO.getComment());
            container.getItems().clear();
            containerDTO.getItems().forEach(containerItemDTO -> {
                ContainerItem containerItem = new ContainerItem();
                containerItem.setQty(containerItemDTO.getQty());
                containerItem.setProductId(containerItemDTO.getProductId());
                container.addItem(containerItem);
            });
            return containerRepository.save(container).getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(Container.class.getSimpleName(), "id", id));
    }

    public ContainerDetailDTO get(Long id) {
        return containerRepository.getDetail(id).map(container -> {
            ContainerDetailDTO containerDetailDTO = new ContainerDetailDTO();
            containerDetailDTO.setId(container.getId());
            containerDetailDTO.setIdentificationNumber(container.getIdentificationNumber());
            containerDetailDTO.setType(container.getType());
            containerDetailDTO.setMaterial(container.getMaterial());
            containerDetailDTO.setSpecialTag(container.getSpecialTag());
            containerDetailDTO.setWeight(container.getCapacity());
            containerDetailDTO.setWidth(container.getWidth());
            containerDetailDTO.setHeight(container.getHeight());
            containerDetailDTO.setDepth(container.getDepth());
            containerDetailDTO.setDiameter(container.getDiameter());
            containerDetailDTO.setComment(container.getComment());
            containerDetailDTO.setItems(container.getItems().stream().map(containerItem -> {
                        ContainerItemDetailDTO containerItemDetailDTO = new ContainerItemDetailDTO();
                        containerItemDetailDTO.setProduct(containerItem.getProduct().toProductCommonDTO());
                        containerItemDetailDTO.setQty(containerItem.getQty());
                        containerItemDetailDTO.setUnit(containerItem.getProduct().getUnit().toDTO());
                        if (containerItem.getProduct().getCategory() != null) {
                            containerItemDetailDTO.setCategory(containerItem.getProduct().getCategory().toCommonDTO());
                        }
                        return containerItemDetailDTO;
                    }).toList()
            );
            return containerDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(Container.class.getSimpleName(), "id", id));
    }

    public Page<ContainerListDTO> getList(BaseFilter filter) {
        ResultList<Container> resultList = containerRepository.getResultList(filter);
        List<ContainerListDTO> result = resultList
                .getList()
                .stream()
                .map(container -> {
                    ContainerListDTO containerListDTO = new ContainerListDTO();
                    containerListDTO.setId(container.getId());
                    containerListDTO.setIdentificationNumber(container.getIdentificationNumber());
                    containerListDTO.setWidth(container.getWidth());
                    containerListDTO.setHeight(container.getHeight());
                    containerListDTO.setDepth(container.getDepth());
                    containerListDTO.setDiameter(container.getDiameter());
                    containerListDTO.setWeight(container.getWeight());
                    containerListDTO.setMaterial(container.getMaterial());
                    containerListDTO.setSpecialTag(container.getSpecialTag());
                    containerListDTO.setComment(container.getComment());
                    return containerListDTO;
                }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    @Transactional
    public void delete(Long id) {
        containerRepository.deleteById(id);
    }

}
