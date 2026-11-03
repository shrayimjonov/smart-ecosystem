package uz.uzkassa.smartpossupply.repository.impl;

import uz.uzkassa.smartpossupply.constants.MessageConstants;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.filters.SimpleFilter;
import uz.uzkassa.smartpossupply.service.LocalizationService;
import uz.uzkassa.smartpossupply.utils.BeanUtils;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;

import java.util.Optional;
import java.util.Set;

public abstract class BaseRepositoryImpl {

    @PersistenceContext(unitName = "slavePU")
    protected EntityManager entityManager;

    @PersistenceContext(unitName = "masterPU")
    protected EntityManager masterEntityManager;

    protected static Long getCompanyId(SimpleFilter filter) {
        return getCompanyId(filter, true);
    }

    protected static Long getCompanyId(SimpleFilter filter, boolean throwException) {
        if (SecurityUtils.isFromAdmin()) {
            throwException = false;
        }
        Long companyId = Optional.ofNullable(filter.getCompanyId()).orElse(SecurityUtils.getCurrentCompanyId());
        if (companyId == null && throwException) {
            LocalizationService localizationService = BeanUtils.getBean(LocalizationService.class);
            throw new BadRequestException(localizationService.localize(MessageConstants.ERROR), localizationService.localize(MessageConstants.BAD_REQUEST_FROM_CLIENT));
        }
        return companyId;
    }

    protected static Long getBranchId(SimpleFilter filter) {
        return getBranchId(filter, true);
    }

    protected static Long getBranchId(SimpleFilter filter, boolean throwException) {
        Long branchId = Optional.ofNullable(filter.getBranchId()).orElse(SecurityUtils.getCurrentBranchId());
        if (branchId == null && throwException) {
            LocalizationService localizationService = BeanUtils.getBean(LocalizationService.class);
            throw new BadRequestException(localizationService.localize(MessageConstants.BAD_REQUEST_FROM_CLIENT));
        }
        return branchId;
    }

    protected static Set<Long> getBranchIds(boolean throwException) {
        Set<Long> branchIds = SecurityUtils.getCurrentBranchIds();
        if (CollectionUtils.isEmpty(branchIds) && throwException) {
            LocalizationService localizationService = BeanUtils.getBean(LocalizationService.class);
            throw new BadRequestException(localizationService.localize(MessageConstants.BAD_REQUEST_FROM_CLIENT));
        }
        return branchIds;
    }

    protected static Long getWarehouseId(BaseFilter filter) {
        return getWarehouseId(filter, true);
    }

    protected static Long getWarehouseId(BaseFilter filter, boolean throwException) {
        Long warehouseId = Optional.ofNullable(filter.getWarehouseId()).orElse(SecurityUtils.getCurrentWarehouseId());
        if (warehouseId == null && throwException) {
            LocalizationService localizationService = BeanUtils.getBean(LocalizationService.class);
            throw new BadRequestException(localizationService.localize(MessageConstants.BAD_REQUEST_FROM_CLIENT));
        }
        return warehouseId;
    }
}
