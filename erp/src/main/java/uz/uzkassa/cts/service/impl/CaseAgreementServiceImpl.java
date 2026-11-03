package uz.uzkassa.cts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.cases.CaseAgreement;
import uz.uzkassa.cts.domain.cases.CaseXizmat;
import uz.uzkassa.cts.dto.caseAgreement.CaseAgreementDTO;
import uz.uzkassa.cts.dto.integration.billing.ActivateAgreementRequestDto;
import uz.uzkassa.cts.dto.integration.billing.AgreementResponseDTO;
import uz.uzkassa.cts.dto.integration.billing.CreateAgreementDTO;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.billing.AgreementStatus;
import uz.uzkassa.cts.integrations.BillingServiceFeignClient;
import uz.uzkassa.cts.repository.CaseOperationRepository;
import uz.uzkassa.cts.service.CaseAgreementService;
import uz.uzkassa.cts.service.CaseXizmatService;
import uz.uzkassa.cts.web.rest.errors.BadRequestException;


@Slf4j
@Service
@Transactional
public class CaseAgreementServiceImpl extends BaseServiceImpl<CaseAgreement, Long> implements CaseAgreementService {

    private final CaseOperationRepository caseOperationRepository;
    private final BillingServiceFeignClient billingServiceFeignClient;
    private final CaseXizmatService caseTypeXizmatService;

    public CaseAgreementServiceImpl(CaseOperationRepository caseOperationRepository,
                                    BillingServiceFeignClient billingServiceFeignClient,
                                    CaseXizmatService caseTypeXizmatService) {
        super(caseOperationRepository);
        this.caseOperationRepository = caseOperationRepository;
        this.billingServiceFeignClient = billingServiceFeignClient;
        this.caseTypeXizmatService = caseTypeXizmatService;
    }

    @Override
    public void createAgreement(CaseAgreementDTO caseAgreementDTO) {
        if (caseOperationRepository.existsByEntityIdAndEntityType(caseAgreementDTO.getEntityId(), caseAgreementDTO.getEntityType()))
            return;
        CaseXizmat caseTypeXizmat = caseTypeXizmatService.get(caseAgreementDTO.getCaseType(), caseAgreementDTO.getReason(), caseAgreementDTO.getType());
        if (caseTypeXizmat == null) {
            throw validationService.badRequestExceptionThrow(XIZMAT_CASE_TYPE).get();
        }
        CreateAgreementDTO agreementRequestDTO = new CreateAgreementDTO();
        agreementRequestDTO.setInn(caseAgreementDTO.getTin());
        agreementRequestDTO.setXizmatId(caseTypeXizmat.getXizmatId());
        AgreementResponseDTO agreementResponseDTO = billingServiceFeignClient.createAgreement(agreementRequestDTO);
        if (agreementResponseDTO != null) {
            CaseAgreement caseOperations = new CaseAgreement();
            caseOperations.setEntityId(caseAgreementDTO.getEntityId());
            caseOperations.setEntityType(caseAgreementDTO.getEntityType());
            caseOperations.setAgreementId(agreementResponseDTO.getId());
            saveAndFlash(caseOperations);
            this.acceptAgreement(caseAgreementDTO.getEntityId(), caseAgreementDTO.getEntityType());
        } else {
            throw new BadRequestException("Billing response is not successfully");
        }
    }

    @Override
    public void acceptAgreement(Long entityId, EntityTypeEnum entityType) {
//        if (caseOperationRepository.existsByEntityIdAndEntityType(entityId, entityType))
//            return;
        caseOperationRepository
                .findByEntityIdAndEntityType(entityId, entityType)
                .map(caseAgreement -> {
                    ActivateAgreementRequestDto dto = new ActivateAgreementRequestDto();
                    dto.setAgreementId(caseAgreement.getAgreementId());
                    billingServiceFeignClient.activateAgreement(dto);
                    return caseAgreement;
                }).orElseThrow(validationService.badRequestExceptionThrow(CASE_AGREEMENT_PAID_SERVICE));
    }

    @Override
    public CaseAgreement checkAgreementStatus(Long entityId, EntityTypeEnum entityType) {
        return caseOperationRepository
                .findByEntityIdAndEntityType(entityId, entityType)
                .map(caseAgreement -> {
                    if (!AgreementStatus.ACTIVE.equals(caseAgreement.getAgreementStatus())) {
                        AgreementResponseDTO agreementResponseDTO = billingServiceFeignClient.getAgreementObjectById(caseAgreement.getAgreementId());
                        if (agreementResponseDTO != null) {
                            caseAgreement.setAgreementStatus(agreementResponseDTO.getStatus());
                            saveAndFlash(caseAgreement);
                            return caseAgreement;
                        } else {
                            throw new BadRequestException("Billing response is not successfully");
                        }
                    }
                    return caseAgreement;
                }).orElseThrow(validationService.badRequestExceptionThrow(CASE_AGREEMENT_PAID_SERVICE));
    }
}
