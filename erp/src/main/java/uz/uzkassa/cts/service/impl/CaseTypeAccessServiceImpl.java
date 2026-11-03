package uz.uzkassa.cts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.cases.CaseTypeAccess;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.caseTypeAccess.CaseTypeAccessDTO;
import uz.uzkassa.cts.dto.caseTypeAccess.CaseTypeAccessListDTO;
import uz.uzkassa.cts.enums.CaseType;
import uz.uzkassa.cts.enums.Role;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.CaseTypeAccessRepository;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.CaseTypeAccessService;

import java.util.EnumSet;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional
public class CaseTypeAccessServiceImpl extends BaseServiceImpl<CaseTypeAccess, Long> implements CaseTypeAccessService {

    private final CaseTypeAccessRepository typeAccessRepository;

    public CaseTypeAccessServiceImpl(CaseTypeAccessRepository typeAccessRepository) {
        super(typeAccessRepository);
        this.typeAccessRepository = typeAccessRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public EnumSet<CaseType> caseTypesByUserId() {
        if (SecurityUtils.isCurrentUserInRole(Role.SUPER_ADMIN.getRoleCode())) {
            return EnumSet.complementOf(EnumSet.of(CaseType.TARIFF_CHANGE));
        }
        return SecurityUtils.getCurrentUserId() != null ? typeAccessRepository
                .findByEmployeeId(SecurityUtils.getCurrentUserId())
                .stream()
                .map(CaseTypeAccess::getCaseType)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(CaseType.class))) : null;
    }

    @Override
    public void insertUpdate(CaseTypeAccessDTO typeAccessDTO) {
        typeAccessRepository.insertUpdate(StringUtils.join(typeAccessDTO.getCaseTypes(), ","), typeAccessDTO.getEmployeeId());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CaseTypeAccessListDTO> list(BaseFilter filter) {
        ResultList<CaseTypeAccessListDTO> resultList = typeAccessRepository.getResultList(filter);
        return new PageImpl<>(resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }
}
