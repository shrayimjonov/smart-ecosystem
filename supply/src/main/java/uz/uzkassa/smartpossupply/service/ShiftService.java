package uz.uzkassa.smartpossupply.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.shift.Shift;
import uz.uzkassa.smartpossupply.dto.shift.ShiftDTO;
import uz.uzkassa.smartpossupply.enums.ShiftStatus;
import uz.uzkassa.smartpossupply.repository.ShiftRepository;
import uz.uzkassa.smartpossupply.repository.UserSessionRepository;

import java.time.LocalDateTime;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 23.12.2022 16:59
 */
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ShiftService extends BaseService {

    ShiftRepository shiftRepository;
    UserSessionRepository userSessionRepository;

    @Transactional
    public Long create(ShiftDTO shiftDTO) {
        Long branchId = getWebKassaBranchId();
        Shift shift = new Shift();
        shift.setBranchId(branchId);
        shift.setShiftNumber(shiftRepository.getMaxIncrementNumber(branchId) + 1L);
        shift.setFiscalNumber(shiftDTO.getFiscalNumber());
        return shiftRepository.save(shift).getId();
    }

    @Transactional
    public void closeSession() {
        userSessionRepository.findFirstByUserIdAndStatusOrderByCreatedDateDesc(
            getCurrentUserId(),
            ShiftStatus.OPEN
        ).ifPresent(userSession -> {
            userSession.setStatus(ShiftStatus.CLOSED);
            userSession.setEndDate(LocalDateTime.now());
            userSessionRepository.save(userSession);
        });
    }

    @Transactional
    public void close(Long fiscalNumber) {
        closeSession();
        shiftRepository.findFirstByBranchIdAndStatusOrderByCreatedDateDesc(
            getWebKassaBranchId(),
            ShiftStatus.OPEN
        ).ifPresent(shift -> {
            shift.setFiscalNumber(fiscalNumber);
            shift.setEndDate(LocalDateTime.now());
            shift.setStatus(ShiftStatus.CLOSED);
            shiftRepository.save(shift);
        });
    }
}
