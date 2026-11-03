package uz.uzkassa.cts.service.impl;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.uzkassa.cts.domain.AccessoryModel;
import uz.uzkassa.cts.domain.AccessoryModel_;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.dto.accessorymodel.AccessoryModelDTO;
import uz.uzkassa.cts.dto.accessorymodel.AccessoryModelDetailDTO;
import uz.uzkassa.cts.dto.accessorymodel.AccessoryModelListDTO;
import uz.uzkassa.cts.dto.accessorymodel.request.AccessoryModelCreateRequestDto;
import uz.uzkassa.cts.dto.accessorymodel.request.AccessoryModelUpdateRequestDto;
import uz.uzkassa.cts.dto.accessorymodel.response.AccessoryModelResponseDto;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.mappings.AccessoryModelMapper;
import uz.uzkassa.cts.repository.AccessoryModelRepository;
import uz.uzkassa.cts.service.AccessoryModelService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccessoryModelServiceImpl extends BaseServiceImpl<AccessoryModel, Long> implements AccessoryModelService {
    private final AccessoryModelRepository accessoryModelRepository;
    private final AccessoryModelMapper accessoryModelMapper;
    private final ValidationContext validationContext;

    public AccessoryModelServiceImpl(AccessoryModelRepository accessoryModelRepository, AccessoryModelMapper accessoryModelMapper, ValidationContext validationContext) {
        super(accessoryModelRepository);
        this.accessoryModelRepository = accessoryModelRepository;
        this.accessoryModelMapper = accessoryModelMapper;
        this.validationContext = validationContext;
    }

    @Override
    public Page<AccessoryModelResponseDto> list(BaseFilter filter) {
        Specification<AccessoryModel> specification = (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);

            ArrayList<Predicate> list = new ArrayList<>();
            if (StringUtils.isNotEmpty(filter.getSearch())) {
                Path<String> name = root.get(AccessoryModel_.NAME);
                list.add(criteriaBuilder.like(name, filter.getSearchForQuery()));
            }
            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
        Page<AccessoryModel> models = accessoryModelRepository.findAll(specification, filter.getOrderedPageable());
        return models.map(accessoryModelMapper::toDto);
    }


    @Override
    public AccessoryModelResponseDto getById(Long id) {
        return accessoryModelMapper.toDto(findById(id));
    }

    @Override
    public AccessoryModelResponseDto create(AccessoryModelCreateRequestDto dto) {
        AccessoryModel model = accessoryModelMapper.toEntity(dto);
        save(model);
        return accessoryModelMapper.toDto(model);
    }

    @Override
    public AccessoryModelResponseDto update(AccessoryModelUpdateRequestDto dto) {
        AccessoryModel model = findById(dto.getId());
        accessoryModelMapper.toEntity(dto, model);
        save(model);
        return accessoryModelMapper.toDto(model);
    }

    @Override
    public Long create(AccessoryModelDTO accessoryModelDTO) {
        validationContext.validate(accessoryModelDTO);
        AccessoryModel accessoryModel = new AccessoryModel();
        accessoryModel.setName(accessoryModelDTO.getName());
        accessoryModel.setDescription(accessoryModelDTO.getDescription());
        accessoryModel.setStatus(CommonStatus.ACTIVE);
        return accessoryModelRepository.save(accessoryModel).getId();
    }

    @Override
    public Long update(Long id, AccessoryModelDTO accessoryModelDTO) {
        validationContext.validate(accessoryModelDTO);
        AccessoryModel accessoryModel = accessoryModelRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(MODEL));
        accessoryModel.setName(accessoryModelDTO.getName());
        accessoryModel.setDescription(accessoryModelDTO.getDescription());
        return accessoryModelRepository.save(accessoryModel).getId();
    }

    @Override
    public AccessoryModelDetailDTO get(Long id) {
        return accessoryModelRepository.findById(id).map(accessoryModel -> {
            AccessoryModelDetailDTO accessoryModelDetailDTO = new AccessoryModelDetailDTO();
            accessoryModelDetailDTO.setId(accessoryModel.getId());
            accessoryModelDetailDTO.setName(accessoryModel.getName());
            accessoryModelDetailDTO.setDescription(accessoryModel.getDescription());
            accessoryModelDetailDTO.setStatus(accessoryModel.getStatus());
            return accessoryModelDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(MODEL));
    }

    @Override
    public Page<AccessoryModelListDTO> getList(BaseFilter filter) {
        ResultList<AccessoryModel> resultList = accessoryModelRepository.getResultList(filter);
        List<AccessoryModelListDTO> result = resultList.getList().stream().map(accessoryModel -> {
            AccessoryModelListDTO accessoryModelListDTO = new AccessoryModelListDTO();
            accessoryModelListDTO.setId(accessoryModel.getId());
            accessoryModelListDTO.setName(accessoryModel.getName());
            accessoryModelListDTO.setDescription(accessoryModel.getDescription());
            accessoryModelListDTO.setStatus(accessoryModel.getStatus());
            return accessoryModelListDTO;
        }).collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public List<CommonDTO> lookUp(BaseFilter filter) {
        filter.setStatus(CommonStatus.ACTIVE.getCode());
        return accessoryModelRepository.getResultList(filter).getList().stream().map(SimpleEntity::toCommonDTO).toList();
    }

    @Override
    public void updateStatus(Long id, CommonStatus status) {
        accessoryModelRepository.findById(id).ifPresentOrElse(customerRequest -> {
            customerRequest.setStatus(status);
            accessoryModelRepository.save(customerRequest);
        }, () -> validationService.entityNotFoundExceptionThrow(MODEL).get());
    }
}
