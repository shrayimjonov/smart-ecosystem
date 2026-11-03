package uz.uzkassa.cts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.cases.CaseXizmat;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.caseXizmat.CaseXizmatDTO;
import uz.uzkassa.cts.dto.caseXizmat.CaseXizmatDetailAdminDTO;
import uz.uzkassa.cts.dto.caseXizmat.CaseXizmatListAdminDTO;
import uz.uzkassa.cts.enums.CaseType;
import uz.uzkassa.cts.enums.CaseXizmatType;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.FMReportReason;
import uz.uzkassa.cts.filter.CaseXizmatFilter;
import uz.uzkassa.cts.repository.CaseXizmatRepository;
import uz.uzkassa.cts.service.CaseXizmatService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class CaseXizmatServiceImpl extends BaseServiceImpl<CaseXizmat, Long> implements CaseXizmatService {

    private final CaseXizmatRepository caseXizmatRepository;
    private final ValidationContext validationContext;

    public CaseXizmatServiceImpl(CaseXizmatRepository repository, ValidationContext validationContext) {
        super(repository);
        this.caseXizmatRepository = repository;
        this.validationContext = validationContext;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CaseXizmatListAdminDTO> getListForAdmin(CaseXizmatFilter filter) {
        ResultList<CaseXizmat> resultList = caseXizmatRepository.getResultList(filter);
        List<CaseXizmatListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(caseXizmat -> {
                    CaseXizmatListAdminDTO caseXizmatListAdminDTO = new CaseXizmatListAdminDTO();
                    caseXizmatListAdminDTO.setId(caseXizmat.getId());
                    caseXizmatListAdminDTO.setName(caseXizmat.getName());
                    caseXizmatListAdminDTO.setStatus(caseXizmat.getStatus());
                    caseXizmatListAdminDTO.setType(caseXizmat.getType());
                    caseXizmatListAdminDTO.setReason(caseXizmat.getReason());
                    caseXizmatListAdminDTO.setCaseType(caseXizmat.getCaseType());
                    caseXizmatListAdminDTO.setXizmatId(caseXizmat.getXizmatId());
                    caseXizmatListAdminDTO.setXizmatName(caseXizmat.getXizmatName());
                    caseXizmatListAdminDTO.setGrouped(caseXizmat.isGrouped());
                    caseXizmatListAdminDTO.setCreatedDate(caseXizmat.getCreatedDate());
                    return caseXizmatListAdminDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public Long create(CaseXizmatDTO caseTypeXizmatDTO) {
        validationContext.validate(caseTypeXizmatDTO);
        switch (caseTypeXizmatDTO.getType()) {
            case CASE -> {
                if (caseTypeXizmatDTO.getReason() != null && caseXizmatRepository.existsByCaseTypeAndReasonAndStatus(caseTypeXizmatDTO.getCaseType(), caseTypeXizmatDTO.getReason(), CommonStatus.ACTIVE)) {
                    throw validationService.badRequestExceptionThrow(CASE_AGREEMENT_HAS).get();
                } else if (caseXizmatRepository.existsByCaseTypeAndStatus(caseTypeXizmatDTO.getCaseType(), CommonStatus.ACTIVE)) {
                    throw validationService.badRequestExceptionThrow(CASE_AGREEMENT_HAS).get();
                }
            }
            case REGISTER_FISCAL, REGISTER_TERMINAL -> {
                if (caseXizmatRepository.existsByTypeAndStatus(caseTypeXizmatDTO.getType(), CommonStatus.ACTIVE)) {
                    throw validationService.badRequestExceptionThrow(CASE_AGREEMENT_HAS).get();
                }
            }
        }
        CaseXizmat caseXizmat = new CaseXizmat();
        caseXizmat.setName(caseTypeXizmatDTO.getName());
        caseXizmat.setType(caseTypeXizmatDTO.getType());
        caseXizmat.setCaseType(caseTypeXizmatDTO.getCaseType());
        caseXizmat.setXizmatId(caseTypeXizmatDTO.getXizmatId());
        caseXizmat.setXizmatName(caseTypeXizmatDTO.getXizmatName());
        caseXizmat.setGrouped(caseTypeXizmatDTO.isGrouped());
        caseXizmat.setReason(caseTypeXizmatDTO.getReason());
        save(caseXizmat);
        return caseXizmat.getId();
    }

    @Override
    public Long update(Long id, CaseXizmatDTO caseTypeXizmatDTO) {
        caseTypeXizmatDTO.setId(id);
        validationContext.validate(caseTypeXizmatDTO);
        return caseXizmatRepository
                .findById(id)
                .map(caseXizmat -> {
                    caseXizmat.setName(caseTypeXizmatDTO.getName());
                    caseXizmat.setType(caseTypeXizmatDTO.getType());
                    caseXizmat.setCaseType(caseTypeXizmatDTO.getCaseType());
                    caseXizmat.setReason(caseTypeXizmatDTO.getReason());
                    caseXizmat.setXizmatId(caseTypeXizmatDTO.getXizmatId());
                    caseXizmat.setXizmatName(caseTypeXizmatDTO.getXizmatName());
                    caseXizmat.setGrouped(caseTypeXizmatDTO.isGrouped());
                    return id;
                })
                .orElseThrow(validationService.entityNotFoundExceptionThrow(XIZMAT));
    }

    @Override
    @Transactional(readOnly = true)
    public CaseXizmatDetailAdminDTO getForAdmin(Long id) {
        return caseXizmatRepository
                .findById(id)
                .map(caseXizmat -> {
                    CaseXizmatDetailAdminDTO detailAdminDTO = new CaseXizmatDetailAdminDTO();
                    detailAdminDTO.setId(caseXizmat.getId());
                    detailAdminDTO.setName(caseXizmat.getName());
                    detailAdminDTO.setStatus(caseXizmat.getStatus());
                    detailAdminDTO.setType(caseXizmat.getType());
                    detailAdminDTO.setCaseType(caseXizmat.getCaseType());
                    detailAdminDTO.setReason(caseXizmat.getReason());
                    CommonDTO xizmatDTO = new CommonDTO();
                    xizmatDTO.setId(caseXizmat.getXizmatId());
                    xizmatDTO.setName(caseXizmat.getXizmatName());
                    detailAdminDTO.setXizmat(xizmatDTO);
                    detailAdminDTO.setGrouped(caseXizmat.isGrouped());
                    return detailAdminDTO;
                })
                .orElseThrow(validationService.entityNotFoundExceptionThrow(XIZMAT));
    }

    @Override
    @Transactional(readOnly = true)
    public CaseXizmat get(CaseType caseType, FMReportReason reason, CaseXizmatType type) {
        if (type == CaseXizmatType.CASE) {
            if (reason == null) {
                return caseXizmatRepository.findFirstByCaseTypeAndStatusOrderByIdDesc(caseType, CommonStatus.ACTIVE).orElse(null);
            }
            return caseXizmatRepository.findFirstByCaseTypeAndReasonAndStatusOrderByIdDesc(caseType, reason, CommonStatus.ACTIVE).orElse(null);
        } else {
            return caseXizmatRepository.findFirstByTypeAndStatusOrderByIdDesc(type, CommonStatus.ACTIVE).orElse(null);
        }
    }

    @Override
    public void updateStatus(Long id, CommonStatus status) {
        caseXizmatRepository
                .findById(id)
                .map(caseXizmat -> {
                    caseXizmat.setStatus(status);
                    caseXizmatRepository.save(caseXizmat);
                    return caseXizmat.getId();
                })
                .orElseThrow(validationService.entityNotFoundExceptionThrow(XIZMAT));
    }

    @Override
    public void updateReason(Long id, FMReportReason reason) {
        caseXizmatRepository
                .findById(id)
                .map(caseXizmat -> {
                    caseXizmat.setReason(reason);
                    caseXizmatRepository.save(caseXizmat);
                    return caseXizmat.getId();
                })
                .orElseThrow(validationService.entityNotFoundExceptionThrow(XIZMAT));
    }
}
