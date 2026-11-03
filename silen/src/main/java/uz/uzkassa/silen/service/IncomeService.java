package uz.uzkassa.silen.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.silen.domain.Income;
import uz.uzkassa.silen.domain.Warehouse;
import uz.uzkassa.silen.dto.filter.WarehouseFilter;
import uz.uzkassa.silen.dto.warehouse.IncomeDTO;
import uz.uzkassa.silen.enumeration.IncomeStatus;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.repository.pg.IncomeRepository;

/**
 * Service Implementation for managing {@link Warehouse}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IncomeService extends BaseService {
    private final IncomeRepository incomeRepository;

    @Transactional
    public Long create(final IncomeDTO dto) {
        final String organizationId = getCurrentOrganizationId();
        Income income = new Income();
        income.setOrganizationId(organizationId);
        if (StringUtils.isNotEmpty(dto.getNumber())) {
            if (incomeRepository.existsShipmentByOrganizationIdAndNumberIgnoreCase(organizationId, dto.getNumber())) {
                throw new BadRequestException("Приход с таким номером уже существует");
            } else {
                income.setNumber(dto.getNumber());
            }
        } else {
            Integer intNumber = incomeRepository.getMaxIntNumber(organizationId);
            String number = null;
            if (intNumber == null) {
                intNumber = 1;
            }
            boolean exists = true;
            while (exists) {
                number = "SHIP-" + intNumber;
                if (incomeRepository.existsShipmentByOrganizationIdAndNumberIgnoreCase(organizationId, number)) {
                    intNumber++;
                } else {
                    exists = false;
                }
            }
            income.setIntNumber(intNumber);
            income.setNumber(number);
        }
        income.setStatus(IncomeStatus.NEW);
        income.setStoreId(dto.getStoreId());
        income.setDescription(dto.getDescription());
        income = incomeRepository.save(income);

        return income.getId();
    }

    @Transactional
    public IncomeDTO update(final IncomeDTO dto) {
        Income income = incomeRepository.findById(dto.getId()).orElseThrow(notFound("Income"));
        income.setStoreId(dto.getStoreId());
        income.setDescription(dto.getDescription());
        income = incomeRepository.save(income);

        return new IncomeDTO(income);
    }

    @Transactional
    public void changeStatus(final IncomeDTO dto) {
        Income income = incomeRepository.findById(dto.getId()).orElseThrow(notFound("Income"));
        income.setStatus(dto.getStatus());
        incomeRepository.save(income);
    }

    public Page<IncomeDTO> findAllByFilter(final WarehouseFilter filter) {
        return incomeRepository.findAllByFilter(filter).map(IncomeDTO::new);
    }

    public IncomeDTO getOne(final Long id) {
        return incomeRepository.findById(id)
                .map(IncomeDTO::new)
                .orElseThrow(notFound());
    }

    @Transactional
    public void delete(final Long id) {
        Income income = incomeRepository.findById(id).orElseThrow(notFound("Income"));
        if (income.getStatus() != IncomeStatus.NEW) {
            throw badRequest("Невозможно удалить приход со статусом " + income.getStatus().getText()).get();
        }
        incomeRepository.delete(income);
    }
}
