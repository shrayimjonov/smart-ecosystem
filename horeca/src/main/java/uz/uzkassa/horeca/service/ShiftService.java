package uz.uzkassa.horeca.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.horeca.domain.auth.User;
import uz.uzkassa.horeca.domain.shift.Shift;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.dto.shift.ShiftDTO;
import uz.uzkassa.horeca.dto.shift.ShiftDetailDTO;
import uz.uzkassa.horeca.enums.Prefix;
import uz.uzkassa.horeca.enums.ShiftStatus;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.repository.ShiftRepository;
import uz.uzkassa.horeca.utils.DateUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 23.12.2022 16:59
 */
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ShiftService extends BaseService {

    ShiftRepository shiftRepository;

    public HashMap<String, String> getNumberingMap(Long branchId) {
        Long incrementNumber = shiftRepository.getMaxIncrementNumber(branchId) + 1L;
        String shiftNumber = Prefix.SHIFT
                .getName()
                .concat(DateUtils.formatYearLastTwoDigit(LocalDate.now()))
                .concat("-")
                .concat(COMMON_NUMBERING_FORMAT.format(incrementNumber));

        HashMap<String, String> numberMap = new HashMap<>();
        numberMap.put(INCREMENT_NUMBER, String.valueOf(incrementNumber));
        numberMap.put(NUMBER, shiftNumber);
        return numberMap;
    }

    @Transactional
    public Shift create(ShiftDTO shiftDTO) {
        Long branchId = Optional.ofNullable(shiftDTO.getBranchId()).orElseGet(BaseService::getCurrentBranchId);
        if (branchId == null) {
            throw badRequestExceptionThrow("Select branch").get();
        }
        User currentUser = getCurrentUser();

        if (shiftRepository.existsByUserIdAndStatus(currentUser.getId(), ShiftStatus.OPEN)) {
            throw badRequestExceptionThrow("User has open shift").get();
        }
        Map<String, String> numberingMap = getNumberingMap(branchId);
        Shift shift = new Shift();
        shift.setBranchId(branchId);
        shift.setUserId(currentUser.getId());
        shift.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
        shift.setShiftNumber(Optional.ofNullable(shiftDTO.getShiftNumber()).orElse(numberingMap.get(NUMBER)));
        shiftRepository.save(shift);
        currentUser.setShiftId(shift.getId());
        userRepository.save(currentUser);
        cacheService.clearUserCache(currentUser);
        cacheService.clear(Shift.class.getName());
        return shift;
    }

    @Transactional
    public void close() {
        User currentUser = getCurrentUser();
        shiftRepository.get(currentUser.getShiftId()).ifPresent(shift -> {
            shift.setEndDate(LocalDateTime.now());
            shift.setStatus(ShiftStatus.CLOSED);
            shiftRepository.saveAndFlush(shift);
            cacheService.clear(Shift.class.getName());
        });
    }

    @Transactional
    public ShiftDetailDTO getCurrentShift() {
        User currentUser = getCurrentUser();
        ShiftDetailDTO shiftDTO = new ShiftDetailDTO();
        if (currentUser.getBranchId() != null) {
            Shift shift = shiftRepository.findFirstByUserIdAndStatusOrderByCreatedDateDesc(currentUser.getId(), ShiftStatus.OPEN).orElseGet(() -> create(new ShiftDTO(currentUser.getBranchId())));
            shiftDTO.setId(shift.getId());
            shiftDTO.setShiftNumber(shift.getShiftNumber());
            shiftDTO.setStatus(shift.getStatus());
            shiftDTO.setStartDate(shift.getCreatedDate());
        }
        return shiftDTO;
    }

    public Page<ShiftDetailDTO> getList(BaseFilter filter) {
        ResultList<Shift> resultList = shiftRepository.getResultList(filter);
        List<ShiftDetailDTO> result = new ArrayList<>();
        resultList.getList().forEach(shift -> {
            ShiftDetailDTO dto = new ShiftDetailDTO();
            dto.setId(shift.getId());
            dto.setShiftNumber(shift.getShiftNumber());
            dto.setStatus(shift.getStatus());
            dto.setStartDate(shift.getCreatedDate());
            result.add(dto);
        });
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }
}
