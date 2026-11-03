package uz.uzkassa.cts.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.Approver;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.domain.approval.process.ApprovalProcessStep;
import uz.uzkassa.cts.domain.hrms.BenefitRequest;
import uz.uzkassa.cts.domain.hrms.EquipmentRequest;
import uz.uzkassa.cts.domain.hrms.EventRequest;
import uz.uzkassa.cts.domain.hrms.LeaveRequest;
import uz.uzkassa.cts.dto.approvalprocess.ApprovalProcessHistoryDTO;
import uz.uzkassa.cts.dto.approvalprocess.ApproverDTO;
import uz.uzkassa.cts.dto.approvalprocess.ApproverDetailDTO;
import uz.uzkassa.cts.dto.base.LocalizedMessageDTO;
import uz.uzkassa.cts.dto.payload.NotificationPayload;
import uz.uzkassa.cts.enums.ApproveAction;
import uz.uzkassa.cts.enums.ApproverCommonStatus;
import uz.uzkassa.cts.enums.ApproverStatus;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqNotificationProducer;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.ApprovalProcessHistoryService;
import uz.uzkassa.cts.service.ApproverService;
import uz.uzkassa.cts.service.LeaveAllowanceService;
import uz.uzkassa.cts.service.TemplateService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ApproverServiceImpl extends BaseServiceImpl<Approver, Long> implements ApproverService {

    ApproverRepository approverRepository;
    ApprovalProcessStepRepository approvalProcessStepRepository;
    ApprovalProcessHistoryService approvalProcessHistoryService;
    LeaveRequestRepository leaveRequestRepository;
    BenefitRequestRepository benefitRequestRepository;
    EquipmentRequestRepository equipmentRequestRepository;
    EventRequestRepository eventRequestRepository;
    RabbitMqNotificationProducer rabbitMqNotificationProducer;
    TemplateService templateService;
    LeaveAllowanceService leaveAllowanceService;
    ValidationContext validationContext;

    public ApproverServiceImpl(ApproverRepository approverRepository, ApprovalProcessStepRepository approvalProcessStepRepository,
                               ApprovalProcessHistoryService approvalProcessHistoryService, LeaveRequestRepository leaveRequestRepository,
                               BenefitRequestRepository benefitRequestRepository, EquipmentRequestRepository equipmentRequestRepository, EventRequestRepository eventRequestRepository,
                               RabbitMqNotificationProducer rabbitMqNotificationProducer, TemplateService templateService, LeaveAllowanceService leaveAllowanceService, ValidationContext validationContext) {
        super(approverRepository);
        this.approverRepository = approverRepository;
        this.approvalProcessStepRepository = approvalProcessStepRepository;
        this.approvalProcessHistoryService = approvalProcessHistoryService;
        this.leaveRequestRepository = leaveRequestRepository;
        this.benefitRequestRepository = benefitRequestRepository;
        this.equipmentRequestRepository = equipmentRequestRepository;
        this.eventRequestRepository = eventRequestRepository;
        this.rabbitMqNotificationProducer = rabbitMqNotificationProducer;
        this.templateService = templateService;
        this.leaveAllowanceService = leaveAllowanceService;
        this.validationContext = validationContext;
    }

    private void validateApprover(Approver approver, ApproverStatus status) {
        if (!Objects.equals(approver.getEmployeeId(), SecurityUtils.getCurrentUserId()) && !isCreator(approver, status)) {
            throw validationService.badRequestExceptionThrow(INVALID_ACTION).get();
        }
    }

    private boolean isCreator(Approver approver, ApproverStatus status) {
        User creator = this.getCreator(approver);
        return Objects.equals(creator.getId(), SecurityUtils.getCurrentUserId()) && ApproverStatus.WAITING_FOR_APPROVAL == status;
    }

    @Override
    public void create(EntityTypeEnum entityType, Long entityId, List<ApproverDTO> approverDTOList) {
        for (ApproverDTO approverDTO : approverDTOList) {
            approvalProcessStepRepository.findById(approverDTO.getStepId())
                    .ifPresentOrElse(approvalProcessStep -> {
                                if (entityType != approvalProcessStep.getApprovalProcess().getEntityType()) {
                                    throw validationService.badRequestExceptionThrow(ENTITY_TYPE_MISMATCH).get();
                                }
                                Approver approver = new Approver();
                                approver.setEntityId(entityId);
                                approver.setEntityType(entityType);
                                approver.setEmployeeId(approverDTO.getEmployeeId());
                                approver.setApprovalProcessStepId(approvalProcessStep.getId());
                                approver.setStep(approvalProcessStep.getStep());
                                if (approvalProcessStep.getStep() == 1) {
                                    approver.setCurrent(true);
                                }
                                approverRepository.save(approver);
                            }
                            , () -> validationService.entityNotFoundExceptionThrow(STEPS).get()
                    );
        }
    }

    @Override
    public void updateStatus(EntityTypeEnum entityType, Long entityId, ApproverStatus status) {
        approverRepository.findCurrentApprover(entityId, entityType).ifPresentOrElse(approver -> {
            this.validateApprover(approver, status);

            ApprovalProcessStep approvalProcessStep = approver.getApprovalProcessStep();
            approver.setStatus(status);

            switch (status) {
                case WAITING_FOR_APPROVAL ->
                        this.handleWaitingForApproval(approver);
                case APPROVED ->
                        this.handleApproval(approver, approvalProcessStep.getApproveAction(), status);
                case REJECTED ->
                        this.handleRejection(approver, approvalProcessStep.getRejectAction(), status);
            }

            approverRepository.save(approver);
            this.createHistory(approver, status);
        }, () -> validationService.entityNotFoundExceptionThrow(APPROVER).get());
    }

    @Override
    public ApproverDetailDTO getCurrentApprover(EntityTypeEnum entityType, Long entityId) {
        return approverRepository.findCurrentApprover(entityId, entityType)
                .map(currentApprover -> {
                    ApproverDetailDTO approverDetailDTO = new ApproverDetailDTO();
                    approverDetailDTO.setId(currentApprover.getId());
                    approverDetailDTO.setEmployee(currentApprover.getEmployee().toCommonDTO());
                    approverDetailDTO.setStatus(currentApprover.getStatus());
                    approverDetailDTO.setCurrent(currentApprover.isCurrent());
                    approverDetailDTO.setStep(currentApprover.getStep());
                    if (Objects.equals(currentApprover.getEmployeeId(), SecurityUtils.getCurrentUserId())) {
                        approverDetailDTO.setCanAction(true);
                    }
                    return approverDetailDTO;
                })
                .orElse(null);
    }

    private void handleWaitingForApproval(Approver approver) {
        this.updateEntityStatus(approver, ApproverCommonStatus.WAITING_FOR_APPROVAL);
        this.sendNotificationToApprover(approver);
    }

    private void handleApproval(Approver approver, ApproveAction approveAction, ApproverStatus status) {
        switch (approveAction) {
            case MARK_AS_APPROVED -> {
                this.updateEntityStatus(approver, ApproverCommonStatus.APPROVED);
                this.sendNotificationToCreator(approver, status);
            }
            case SEND_TO_NEXT_APPROVER ->
                    this.moveToNextApprover(approver);
            default ->
                    throw new IllegalStateException("Unexpected ApproveAction: " + approveAction);
        }
    }

    private void handleRejection(Approver approver, ApproveAction approveAction, ApproverStatus status) {
        switch (approveAction) {
            case MARK_AS_REJECTED -> {
                this.updateEntityStatus(approver, ApproverCommonStatus.REJECTED);
                this.sendNotificationToCreator(approver, status);
            }
            case SEND_TO_PREVIOUS_APPROVER ->
                    this.moveToPreviousApprover(approver);
            default ->
                    throw new IllegalStateException("Unexpected RejectAction: " + approveAction);
        }
    }

    private void updateEntityStatus(Approver approver, ApproverCommonStatus status) {
        switch (approver.getEntityType()) {
            case LEAVE_REQUEST ->
                    leaveRequestRepository.findById(approver.getEntityId()).ifPresent(leaveRequest -> {
                        leaveRequest.setStatus(status);
                        leaveRequestRepository.save(leaveRequest);
                        if (ApproverCommonStatus.APPROVED == status) {
                            leaveAllowanceService.recalculate(leaveRequest.getCreatorId(), leaveRequest.getDuration(), leaveRequest.getFromDate().getYear());
                        }
                    });
            case BENEFIT_REQUEST ->
                    benefitRequestRepository.findById(approver.getEntityId()).ifPresent(benefitRequest -> {
                        benefitRequest.setStatus(status);
                        benefitRequestRepository.save(benefitRequest);
                    });
            case EQUIPMENT_REQUEST ->
                    equipmentRequestRepository.findById(approver.getEntityId()).ifPresent(equipmentRequest -> {
                        equipmentRequest.setStatus(status);
                        equipmentRequestRepository.save(equipmentRequest);
                    });
            case EVENT_REQUEST ->
                    eventRequestRepository.findById(approver.getEntityId()).ifPresent(eventRequest -> {
                        eventRequest.setStatus(status);
                        eventRequestRepository.save(eventRequest);
                    });
            default ->
                    throw new IllegalStateException("Unexpected Entity Type: " + approver.getEntityType());
        }
    }

    private void moveToNextApprover(Approver currentApprover) {
        currentApprover.setCurrent(false);
        approverRepository.findByStepAndEntityIdAndEntityType(currentApprover.getStep() + 1, currentApprover.getEntityId(), currentApprover.getEntityType()).ifPresentOrElse(nextApprover -> {
            nextApprover.setCurrent(true);
            nextApprover.setStatus(ApproverStatus.WAITING_FOR_APPROVAL);
            approverRepository.save(nextApprover);
            this.sendNotificationToApprover(nextApprover);
        }, () -> validationService.entityNotFoundExceptionThrow(APPROVER).get());
    }

    private void moveToPreviousApprover(Approver currentApprover) {
        currentApprover.setCurrent(false);
        approverRepository.findByStepAndEntityIdAndEntityType(currentApprover.getStep() - 1, currentApprover.getEntityId(), currentApprover.getEntityType()).ifPresentOrElse(previousApprover -> {
            previousApprover.setCurrent(true);
            previousApprover.setStatus(ApproverStatus.WAITING_FOR_APPROVAL);
            approverRepository.save(previousApprover);
            this.sendNotificationToApprover(previousApprover);
        }, () -> validationService.entityNotFoundExceptionThrow(APPROVER).get());
    }

    private void createHistory(Approver approver, ApproverStatus status) {
        ApprovalProcessHistoryDTO approvalProcessHistoryDTO = new ApprovalProcessHistoryDTO();
        approvalProcessHistoryDTO.setEntityId(approver.getEntityId());
        approvalProcessHistoryDTO.setEntityType(approver.getEntityType());
        approvalProcessHistoryDTO.setApproverId(SecurityUtils.getCurrentUserId());
        approvalProcessHistoryDTO.setStatus(status);
        approvalProcessHistoryService.create(approvalProcessHistoryDTO);
    }

    private void sendNotificationToCreator(Approver approver, ApproverStatus status) {
        User creator = getCreator(approver);
        LocalizedMessageDTO localizedMessageDTO = templateService.getApprovalProcessMessageForCreator(approver, status);
        this.sendNotificationToQueue(creator.getId(), approver, localizedMessageDTO);
    }

    private void sendNotificationToApprover(Approver approver) {
        User creator = getCreator(approver);
        LocalizedMessageDTO localizedMessageDTO = templateService.getApprovalProcessMessageForApprover(approver, creator);
        this.sendNotificationToQueue(approver.getEmployeeId(), approver, localizedMessageDTO);
    }

    private User getCreator(Approver approver) {
        return switch (approver.getEntityType()) {
            case LEAVE_REQUEST ->
                    leaveRequestRepository.findById(approver.getEntityId())
                            .map(LeaveRequest::getCreator)
                            .orElseThrow(() -> validationService.entityNotFoundExceptionThrow(LeaveRequest.class.getSimpleName()).get());
            case BENEFIT_REQUEST ->
                    benefitRequestRepository.findById(approver.getEntityId())
                            .map(BenefitRequest::getCreator)
                            .orElseThrow(() -> validationService.entityNotFoundExceptionThrow(BenefitRequest.class.getSimpleName()).get());
            case EQUIPMENT_REQUEST ->
                    equipmentRequestRepository.findById(approver.getEntityId())
                            .map(EquipmentRequest::getCreator)
                            .orElseThrow(() -> validationService.entityNotFoundExceptionThrow(EquipmentRequest.class.getSimpleName()).get());
            case EVENT_REQUEST ->
                    eventRequestRepository.findById(approver.getEntityId())
                            .map(EventRequest::getCreator)
                            .orElseThrow(() -> validationService.entityNotFoundExceptionThrow(EventRequest.class.getSimpleName()).get());
            default ->
                    throw new IllegalStateException("Unexpected Entity Type: " + approver.getEntityType());
        };
    }

    private void sendNotificationToQueue(Long receiverId, Approver approver, LocalizedMessageDTO localizedMessageDTO) {
        rabbitMqNotificationProducer.sendToQueue(NotificationPayload.builder()
                .companyId(SecurityUtils.getCompanyId())
                .entityId(approver.getEntityId())
                .entityType(approver.getEntityType())
                .receiverId(receiverId)
                .messageRu(localizedMessageDTO.getMessageRu())
                .messageUzLatin(localizedMessageDTO.getMessageUzLatin())
                .messageUzCyrillic(localizedMessageDTO.getMessageUzCyrillic())
                .build());
    }
}
