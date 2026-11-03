package uz.uzkassa.cts.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.approval.process.ApprovalProcess;
import uz.uzkassa.cts.domain.approval.process.ApprovalProcessApprover;
import uz.uzkassa.cts.domain.approval.process.ApprovalProcessStep;
import uz.uzkassa.cts.dto.approvalprocess.ApprovalProcessApproverDetailAdminDTO;
import uz.uzkassa.cts.dto.approvalprocess.ApprovalProcessDTO;
import uz.uzkassa.cts.dto.approvalprocess.ApprovalProcessDetailAdminDTO;
import uz.uzkassa.cts.dto.approvalprocess.ApprovalProcessStepDetailAdminDTO;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.repository.ApprovalProcessApproverRepository;
import uz.uzkassa.cts.repository.ApprovalProcessRepository;
import uz.uzkassa.cts.repository.ApprovalProcessStepRepository;
import uz.uzkassa.cts.service.ApprovalProcessService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class ApprovalProcessServiceImpl extends BaseServiceImpl<ApprovalProcess, Long> implements ApprovalProcessService {

    private final ApprovalProcessRepository approvalProcessRepository;
    private final ApprovalProcessStepRepository approvalProcessStepRepository;
    private final ApprovalProcessApproverRepository approvalProcessApproverRepository;
    private final ValidationContext validationContext;

    public ApprovalProcessServiceImpl(ApprovalProcessRepository approvalProcessRepository, ApprovalProcessStepRepository approvalProcessStepRepository, ApprovalProcessApproverRepository approvalProcessApproverRepository, ValidationContext validationContext) {
        super(approvalProcessRepository);
        this.approvalProcessRepository = approvalProcessRepository;
        this.approvalProcessStepRepository = approvalProcessStepRepository;
        this.approvalProcessApproverRepository = approvalProcessApproverRepository;
        this.validationContext = validationContext;
    }

    @Override
    @Transactional
    public Long create(ApprovalProcessDTO approvalProcessDTO) {
        validationContext.validate(approvalProcessDTO);
        ApprovalProcess approvalProcess = new ApprovalProcess();
        approvalProcess.setEntityType(approvalProcessDTO.getEntityType());
        approvalProcess.setCompanyId(approvalProcessDTO.getCompanyId());
        approvalProcessRepository.save(approvalProcess);
        approvalProcessDTO.getSteps().forEach(approvalProcessStepDTO -> {
            ApprovalProcessStep approvalProcessStep = new ApprovalProcessStep();
            approvalProcessStep.setApprovalProcessId(approvalProcess.getId());
            approvalProcessStep.setStep(approvalProcessStepDTO.getStep());
            approvalProcessStep.setApproveAction(approvalProcessStepDTO.getApproveAction());
            approvalProcessStep.setRejectAction(approvalProcessStepDTO.getRejectAction());
            approvalProcessStepRepository.save(approvalProcessStep);

            List<ApprovalProcessApprover> approvalProcessApprovers = approvalProcessStepDTO
                    .getApprovers()
                    .stream()
                    .map(approvalProcessApproverDTO -> {
                        ApprovalProcessApprover approvalProcessApprover = new ApprovalProcessApprover();
                        approvalProcessApprover.setApprovalProcessStepId(approvalProcessStep.getId());
                        approvalProcessApprover.setType(approvalProcessApproverDTO.getType());
                        approvalProcessApprover.setRole(approvalProcessApproverDTO.getRole());
                        approvalProcessApprover.setEmployeeId(approvalProcessApproverDTO.getEmployeeId());
                        approvalProcessApprover.setApproveForAll(approvalProcessApproverDTO.isApproveForAll());
                        return approvalProcessApprover;
                    }).collect(Collectors.toList());
            approvalProcessApproverRepository.saveAll(approvalProcessApprovers);
        });
        return approvalProcess.getId();
    }

    @Override
    @Transactional
    public Long update(Long id, ApprovalProcessDTO approvalProcessDTO) {
        approvalProcessDTO.setId(id);
        validationContext.validate(approvalProcessDTO);
        return approvalProcessRepository.findById(id).map(approvalProcess -> {
            List<Long> approvalProcessStepIds = approvalProcess.getSteps().stream().map(approvalProcessStep -> {
                approvalProcessApproverRepository.deleteAllByApprovalProcessStepId(approvalProcessStep.getId());
                return approvalProcessStep.getId();
            }).toList();
            approvalProcessStepRepository.deleteAllByApprovalProcessIdIn(approvalProcessStepIds);

            approvalProcessDTO.getSteps().forEach(approvalProcessStepDTO -> {
                ApprovalProcessStep approvalProcessStep = new ApprovalProcessStep();
                approvalProcessStep.setApprovalProcessId(approvalProcess.getId());
                approvalProcessStep.setStep(approvalProcessStepDTO.getStep());
                approvalProcessStep.setApproveAction(approvalProcessStepDTO.getApproveAction());
                approvalProcessStep.setRejectAction(approvalProcessStepDTO.getRejectAction());
                approvalProcessStepRepository.save(approvalProcessStep);

                List<ApprovalProcessApprover> approvalProcessApprovers = approvalProcessStepDTO
                        .getApprovers()
                        .stream()
                        .map(approvalProcessApproverDTO -> {
                            ApprovalProcessApprover approvalProcessApprover = new ApprovalProcessApprover();
                            approvalProcessApprover.setApprovalProcessStepId(approvalProcessStep.getId());
                            approvalProcessApprover.setType(approvalProcessApproverDTO.getType());
                            approvalProcessApprover.setRole(approvalProcessApproverDTO.getRole());
                            approvalProcessApprover.setEmployeeId(approvalProcessApproverDTO.getEmployeeId());
                            approvalProcessApprover.setApproveForAll(approvalProcessApproverDTO.isApproveForAll());
                            return approvalProcessApprover;
                        }).collect(Collectors.toList());
                approvalProcessApproverRepository.saveAll(approvalProcessApprovers);
            });
            return id;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(APPROVE));
    }

    @Override
    public ApprovalProcessDetailAdminDTO getByEntityTypeAndCompany(EntityTypeEnum entityType, Long companyId) {
        return approvalProcessRepository.findFirstByCompanyIdAndEntityTypeAndDeletedIsFalse(companyId, entityType).map(approvalProcess -> {
            ApprovalProcessDetailAdminDTO approvalProcessDetailAdminDTO = new ApprovalProcessDetailAdminDTO();
            approvalProcessDetailAdminDTO.setId(approvalProcess.getId());
            approvalProcessDetailAdminDTO.setEntityType(approvalProcess.getEntityType());
            approvalProcessDetailAdminDTO.setCompany(approvalProcess.getCompany().toShortDTO());
            List<ApprovalProcessStepDetailAdminDTO> steps = approvalProcess
                    .getSteps()
                    .stream()
                    .map(approvalProcessStep -> {
                        ApprovalProcessStepDetailAdminDTO approvalProcessStepDetailAdminDTO = new ApprovalProcessStepDetailAdminDTO();
                        approvalProcessStepDetailAdminDTO.setId(approvalProcessStep.getId());
                        approvalProcessStepDetailAdminDTO.setStep(approvalProcessStep.getStep());
                        approvalProcessStepDetailAdminDTO.setApproveAction(approvalProcessStep.getApproveAction());
                        approvalProcessStepDetailAdminDTO.setRejectAction(approvalProcessStep.getRejectAction());

                        List<ApprovalProcessApproverDetailAdminDTO> approvers = approvalProcessStep.getApprovalProcessApprovers().stream().map(approver -> {
                            ApprovalProcessApproverDetailAdminDTO approvalProcessApproverDetailAdminDTO = new ApprovalProcessApproverDetailAdminDTO();
                            approvalProcessApproverDetailAdminDTO.setId(approver.getId());
                            approvalProcessApproverDetailAdminDTO.setType(approver.getType());
                            approvalProcessApproverDetailAdminDTO.setRole(approver.getRole());
                            approvalProcessApproverDetailAdminDTO.setEmployee(approver.getEmployeeId() != null ? approver.getEmployee().toCommonDTO() : null);
                            approvalProcessApproverDetailAdminDTO.setApproveForAll(approver.isApproveForAll());
                            return approvalProcessApproverDetailAdminDTO;
                        }).collect(Collectors.toList());
                        approvalProcessStepDetailAdminDTO.setApprovers(approvers);
                        return approvalProcessStepDetailAdminDTO;
                    }).collect(Collectors.toList());
            approvalProcessDetailAdminDTO.setSteps(steps);
            return approvalProcessDetailAdminDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(APPROVE));
    }
}
