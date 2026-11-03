package uz.uzkassa.horeca.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.horeca.domain.hall.Hall;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.dto.hall.HallDTO;
import uz.uzkassa.horeca.dto.hall.HallDetailDTO;
import uz.uzkassa.horeca.dto.hall.HallListDTO;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.enums.OrderStatus;
import uz.uzkassa.horeca.enums.ReservationStatus;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.interfaces.CrudAdvanced;
import uz.uzkassa.horeca.repository.HallItemRepository;
import uz.uzkassa.horeca.repository.HallRepository;
import uz.uzkassa.horeca.repository.OrderRepository;
import uz.uzkassa.horeca.repository.ReservationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class HallService
        extends BaseService
        implements CrudAdvanced<Long, HallDTO, HallDetailDTO, HallListDTO, BaseFilter, CommonStatus, CommonDTO> {

    HallRepository hallRepository;
    HallItemRepository hallItemRepository;
    OrderRepository orderRepository;
    ReservationRepository reservationRepository;

    @Override
    public void validate(HallDTO hallDTO) {
        Long branchId = getCurrentBranchId(hallDTO.getBranchId());
        if (StringUtils.isEmpty(hallDTO.getName())) {
            throw badRequestExceptionThrow("Name is required").get();
        }
        if (hallDTO.getId() == null && hallRepository.existsByBranchIdAndDeletedIsFalseAndNameIgnoreCase(branchId, hallDTO.getName())) {
            throw conflictExceptionThrow("Зал с таким названием " + hallDTO.getName() + " уже существует.").get();
        }
        if (
                hallDTO.getId() != null &&
                        hallRepository.existsByBranchIdAndDeletedIsFalseAndNameIgnoreCaseAndIdNot(branchId, hallDTO.getName(), hallDTO.getId())
        ) {
            throw conflictExceptionThrow("Зал с таким названием " + hallDTO.getName() + " уже существует.").get();
        }
    }

    @Transactional
    @Override
    public Long create(HallDTO hallDTO) {
        this.validate(hallDTO);
        Hall hall = new Hall();
        hall.setName(hallDTO.getName());
        hall.setBranchId(getCurrentBranchId(hallDTO.getBranchId()));
        hallRepository.save(hall);
        return hall.getId();
    }

    @Override
    public Page<HallListDTO> getList(BaseFilter filter) {
        ResultList<Hall> resultList = hallRepository.getResultList(filter);
        List<HallListDTO> result = resultList
                .getList()
                .stream()
                .map(hall -> {
                    HallListDTO hallListDTO = new HallListDTO();
                    hallListDTO.setId(hall.getId());
                    hallListDTO.setName(hall.getName());
                    hallListDTO.setStatus(hall.getStatus());
                    hallListDTO.setBranch(hall.getBranch().toCommonDTO());
                    return hallListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    @Override
    public HallDetailDTO get(Long id) {
        return hallRepository
                .findById(id)
                .map(hall -> {
                    HallDetailDTO hallDetailDTO = new HallDetailDTO();
                    hallDetailDTO.setId(hall.getId());
                    hallDetailDTO.setName(hall.getName());
                    hallDetailDTO.setStatus(hall.getStatus());
                    hallDetailDTO.setBranch(hall.getBranch().toCommonDTO());
                    return hallDetailDTO;
                })
                .orElseThrow(notFoundExceptionThrow(Hall.class.getSimpleName(), "id", id));
    }

    @Override
    @Transactional
    public Long update(Long id, HallDTO hallDTO) {
        return hallRepository
                .findById(id)
                .map(hall -> {
                    hallDTO.setId(id);
                    this.validate(hallDTO);
                    hall.setName(hallDTO.getName());
                    hall.setBranchId(getCurrentBranchId(hallDTO.getBranchId()));
                    hall.getEmployees().clear();
                    hallRepository.save(hall);
                    return hall.getId();
                })
                .orElseThrow(notFoundExceptionThrow(Hall.class.getSimpleName(), "id", id));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        hallRepository.findById(id).map(hall -> {
                    hallItemRepository.findAllByHallIdAndDeletedIsFalseOrderByIdAsc(hall.getId())
                            .forEach(hallItem -> {
                                if (orderRepository.existsByTableIdAndDeletedIsFalse(hallItem.getId())) {
                                    throw badRequestExceptionThrow(localizationService.localize("hall.delete.not.allowed")).get();
                                }
                                if (reservationRepository.existsByTableIdAndDeletedIsFalse(hallItem.getId())) {
                                    throw badRequestExceptionThrow(localizationService.localize("hall.delete.not.allowed")).get();
                                }
                            });
                    hall.setStatus(CommonStatus.IN_ACTIVE);
                    hallRepository.save(hall);
                    return hall;
                })
                .orElseThrow(notFoundExceptionThrow(Hall.class.getSimpleName(), "id", id));
    }

    @Override
    public List<CommonDTO> lookUp(BaseFilter filter) {
        filter.setStatus(CommonStatus.ACTIVE.getCode());
        return hallRepository.getResultList(filter).getList().stream().map(Hall::toCommonDTO).collect(Collectors.toList());
    }

    public List<CommonDTO> getItems(BaseFilter filter) {
        filter.setStatus(CommonStatus.ACTIVE.getCode());
        filter.setSize(100);
        return hallRepository.getResultList(filter).getList().stream().map(Hall::toCommonDTO).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void updateStatus(Long id, CommonStatus status) {
        hallRepository
                .findById(id)
                .map(hall -> {
                    if (CommonStatus.IN_ACTIVE.equals(status)) {
                        hallItemRepository.findAllByHallIdAndDeletedIsFalseOrderByIdAsc(hall.getId())
                                .forEach(hallItem -> {
                                    if (orderRepository.existsByTableIdAndDeletedIsFalseAndStatusNotIn(hallItem.getId(), List.of(OrderStatus.CLOSED, OrderStatus.CANCELLED))) {
                                        throw badRequestExceptionThrow(localizationService.localize("hall.delete.not.allowed")).get();
                                    }
                                    if (reservationRepository.existsByTableIdAndDeletedIsFalseAndStatusIn(hallItem.getId(), List.of(ReservationStatus.ACTIVE, ReservationStatus.NEW))) {
                                        throw badRequestExceptionThrow(localizationService.localize("hall.delete.not.allowed")).get();
                                    }
                                });
                    }
                    hall.setStatus(status);
                    hallRepository.save(hall);
                    return hall;
                })
                .orElseThrow(notFoundExceptionThrow(Hall.class.getSimpleName(), "id", id));
    }
}
