package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.cases.CaseAgreement;
import uz.uzkassa.cts.dto.caseAgreement.CaseAgreementDTO;
import uz.uzkassa.cts.enums.EntityTypeEnum;

public interface CaseAgreementService extends BaseService<CaseAgreement, Long> {

    void createAgreement(CaseAgreementDTO caseOperationDTO);

    void acceptAgreement(Long entityId, EntityTypeEnum entityType);

    CaseAgreement checkAgreementStatus(Long entityId, EntityTypeEnum entityType);
}
