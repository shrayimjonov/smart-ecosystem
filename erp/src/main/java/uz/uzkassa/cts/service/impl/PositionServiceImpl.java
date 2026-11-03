package uz.uzkassa.cts.service.impl;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.constants.CacheConstants;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.domain.hrms.Position;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.base.CommonTreeDTO;
import uz.uzkassa.cts.dto.position.PositionDTO;
import uz.uzkassa.cts.dto.position.admin.PositionDetailAdminDTO;
import uz.uzkassa.cts.dto.position.admin.PositionListAdminDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.PositionRepository;
import uz.uzkassa.cts.service.CacheService;
import uz.uzkassa.cts.service.PositionService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.*;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class PositionServiceImpl extends BaseServiceImpl<Position, Long> implements PositionService, CacheConstants {

    private final PositionRepository positionRepository;
    private final CacheService cacheService;
    private final ValidationContext validationContext;

    public PositionServiceImpl(PositionRepository positionRepository, CacheService cacheService, ValidationContext validationContext) {
        super(positionRepository);
        this.positionRepository = positionRepository;
        this.cacheService = cacheService;
        this.validationContext = validationContext;
    }

    @Override
    @Transactional
    public Long create(PositionDTO positionDTO) {
        validationContext.validate(positionDTO);
        Position position = new Position();
        position.setNameUzLatin(positionDTO.getNameUzLatin());
        position.setNameUzCyrillic(positionDTO.getNameUzCyrillic());
        position.setNameRu(positionDTO.getNameRu());
        position.setParentId(positionDTO.getParentId());
        position.setCompanyId(positionDTO.getCompanyId());
        positionRepository.save(position);
        cacheService.clearByEntityType(position.getEntityType());
        return position.getId();
    }

    @Override
    @Transactional
    public Long update(Long id, PositionDTO positionDTO) {
        validationContext.validate(positionDTO);
        return positionRepository.findById(id).map(position -> {
            position.setNameUzLatin(positionDTO.getNameUzLatin());
            position.setNameUzCyrillic(positionDTO.getNameUzCyrillic());
            position.setNameRu(positionDTO.getNameRu());
            if (!Objects.equals(position.getParentId(), positionDTO.getParentId())) {
                position.setParentId(positionDTO.getParentId());
                position.setParent(positionDTO.getParentId() == null ? null : positionRepository.findById(positionDTO.getParentId()).orElseThrow(validationService.entityNotFoundExceptionThrow(POSITION)));
            }
            positionRepository.save(position);
            cacheService.clearByEntityType(position.getEntityType());
            return id;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(POSITION));
    }

    @Override
    @Cacheable(cacheNames = POSITION_LIST_CACHE)
    public List<PositionListAdminDTO> getListForAdmin(BaseFilter filter) {
        return positionRepository.findAllByDeletedIsFalse()
                .stream()
                .map(position -> {
                    PositionListAdminDTO positionListDTO = new PositionListAdminDTO();
                    positionListDTO.setId(position.getId());
                    positionListDTO.setStatus(position.getStatus());
                    positionListDTO.setNameUzLatin(position.getNameUzLatin());
                    positionListDTO.setNameUzCyrillic(position.getNameUzCyrillic());
                    positionListDTO.setNameRu(position.getNameRu());
                    positionListDTO.setCompany(position.getCompany().toShortDTO());
                    if (position.getParentId() != null) {
                        positionListDTO.setParent(position.getParent().toCommonDTO());
                    }
                    return positionListDTO;
                }).collect(Collectors.toList());
    }

    @Override
    public PositionDetailAdminDTO getForAdmin(Long id) {
        return positionRepository.findById(id).map(position -> {
            PositionDetailAdminDTO positionDetailDTO = new PositionDetailAdminDTO();
            positionDetailDTO.setId(position.getId());
            positionDetailDTO.setStatus(position.getStatus());
            positionDetailDTO.setNameUzLatin(position.getNameUzLatin());
            positionDetailDTO.setNameUzCyrillic(position.getNameUzCyrillic());
            positionDetailDTO.setNameRu(position.getNameRu());
            positionDetailDTO.setCompany(position.getCompany().toShortDTO());
            if (position.getParentId() != null) {
                positionDetailDTO.setParent(position.getParent().toCommonDTO());
            }
            return positionDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(POSITION));
    }

    @Override
    @Cacheable(cacheNames = POSITION_ITEMS_CACHE)
    public List<CommonDTO> getItems(Long companyId) {
        return positionRepository.findAllByCompanyIdAndStatusAndDeletedIsFalse(Optional.ofNullable(companyId).orElseGet(BaseServiceImpl::getCompanyId), CommonStatus.ACTIVE)
                .stream()
                .map(SimpleEntity::toCommonDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateStatus(Long id, CommonStatus status) {
        positionRepository
                .findById(id)
                .ifPresentOrElse(position -> {
                    position.setStatus(status);
                    positionRepository.save(position);
                    cacheService.clearByEntityType(position.getEntityType());
                }, () -> validationService.entityNotFoundExceptionThrow(POSITION).get());
    }

    @Override
    public List<CommonTreeDTO> treeList(BaseFilter filter) {
        return recursion(positionRepository.findAllByCompanyIdAndStatusAndDeletedIsFalse(filter.getCompanyId(), CommonStatus.ACTIVE));
    }

    private List<CommonTreeDTO> recursion(List<Position> positions) {
        Map<Long, CommonTreeDTO> positionsMap = positions.stream()
                .collect(Collectors.toMap(Position::getId, position -> new CommonTreeDTO(position.getId(), position.getName())));

        List<CommonTreeDTO> result = new ArrayList<>();
        positions.forEach(position -> {
            CommonTreeDTO positionTreeDTO = positionsMap.get(position.getId());
            if (position.getParentId() == null) {
                result.add(positionTreeDTO);
            } else {
                CommonTreeDTO parentPositionTreeDTO = positionsMap.get(position.getParentId());
                if (parentPositionTreeDTO != null) {
                    parentPositionTreeDTO.getChildren().add(positionTreeDTO);
                }
            }
        });
        return result;
    }
}
