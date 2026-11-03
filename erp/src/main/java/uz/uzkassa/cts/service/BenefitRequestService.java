package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.hrms.BenefitRequest;
import uz.uzkassa.cts.dto.benefitrequest.BenefitRequestDTO;
import uz.uzkassa.cts.dto.benefitrequest.admin.BenefitRequestDetailAdminDTO;
import uz.uzkassa.cts.dto.benefitrequest.admin.BenefitRequestListAdminDTO;
import uz.uzkassa.cts.dto.benefitrequest.cabinet.BenefitRequestDetailCabinetDTO;
import uz.uzkassa.cts.dto.benefitrequest.cabinet.BenefitRequestListCabinetDTO;
import uz.uzkassa.cts.enums.ApproverCommonStatus;
import uz.uzkassa.cts.filter.BenefitRequestFilter;

public interface BenefitRequestService extends BaseService<BenefitRequest, Long> {
    Long create(BenefitRequestDTO benefitRequestDTO);

    Long update(Long id, BenefitRequestDTO benefitRequestDTO);

    BenefitRequestDetailAdminDTO getForAdmin(Long id);

    Page<BenefitRequestListAdminDTO> getListForAdmin(BenefitRequestFilter filter);

    BenefitRequestDetailCabinetDTO getForCabinet(Long id);

    Page<BenefitRequestListCabinetDTO> getListForCabinet(BenefitRequestFilter filter);

    void updateStatus(Long id, ApproverCommonStatus status);
}
