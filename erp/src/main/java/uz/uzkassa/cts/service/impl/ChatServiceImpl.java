package uz.uzkassa.cts.service.impl;

import lombok.experimental.FieldDefaults;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.hrms.*;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.chat.ChatDTO;
import uz.uzkassa.cts.dto.chat.admin.ChatListAdminDTO;
import uz.uzkassa.cts.dto.employee.EmployeeListLoginDTO;
import uz.uzkassa.cts.enums.DocumentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.Role;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.EmployeeFilter;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.service.ChatService;
import uz.uzkassa.cts.service.FileUploadService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static lombok.AccessLevel.PRIVATE;

/**
 * Created date time: 13.02.2025 11:42
 */

@Transactional(readOnly = true)
@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ChatServiceImpl extends BaseServiceImpl<Chat, Long> implements ChatService {

    ChatRepository chatRepository;
    ChatItemRepository chatItemRepository;
    EmployeeRepository employeeRepository;
    FileUploadService fileUploadService;
    ApproverRepository approverRepository;
    LeaveRequestRepository leaveRequestRepository;
    BenefitRequestRepository benefitRequestRepository;
    EquipmentRequestRepository equipmentRequestRepository;
    EventRequestRepository eventRequestRepository;
    ValidationContext validationContext;

    public ChatServiceImpl(
            ChatRepository chatRepository,
            ChatItemRepository chatItemRepository,
            EmployeeRepository employeeRepository,
            FileUploadService fileUploadService,
            ApproverRepository approverRepository,
            LeaveRequestRepository leaveRequestRepository,
            BenefitRequestRepository benefitRequestRepository,
            EquipmentRequestRepository equipmentRequestRepository,
            EventRequestRepository eventRequestRepository, ValidationContext validationContext
    ) {
        super(chatRepository);
        this.chatRepository = chatRepository;
        this.chatItemRepository = chatItemRepository;
        this.employeeRepository = employeeRepository;
        this.fileUploadService = fileUploadService;
        this.approverRepository = approverRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.benefitRequestRepository = benefitRequestRepository;
        this.equipmentRequestRepository = equipmentRequestRepository;
        this.eventRequestRepository = eventRequestRepository;
        this.validationContext = validationContext;
    }

    @Override
    @Transactional
    public void create(ChatDTO chatDTO) {
        validationContext.validate(chatDTO);
        Chat chat = chatRepository.findByEntityIdAndEntityType(chatDTO.getEntityId(), chatDTO.getEntityType()).orElseGet(Chat::new);
        if (chat.getId() == null) {
            chat.setEntityId(chatDTO.getEntityId());
            chat.setEntityType(chatDTO.getEntityType());
            chatRepository.save(chat);
            Set<ChatMember> members = Stream.concat(
                    approverRepository.findAllEmployeeIdsByEntityIdAndEntityType(chatDTO.getEntityId(), chatDTO.getEntityType()).stream(),
                    Stream.of(getRequestCreatorId(chatDTO.getEntityId(), chatDTO.getEntityType()))
            ).map(memberId -> {
                ChatMember member = new ChatMember();
                member.setMemberId(memberId);
                member.setChatId(chat.getId());
                return member;
            }).collect(Collectors.toSet());
            chat.setMembers(members);
        }
        Set<ChatMember> members = chat.getMembers();
        ChatMember chatMember = new ChatMember();
        chatMember.setChatId(chat.getId());
        chatMember.setMemberId(getCurrentUserId());
        members.add(chatMember);

        ChatItem chatItem = new ChatItem();
        chatItem.setChatId(chat.getId());
        chatItem.setMessage(chatDTO.getMessage());
        chatItem.setAttachmentId(chatDTO.getAttachmentId());
        chatItemRepository.save(chatItem);
        if (CollectionUtils.isNotEmpty(chatDTO.getMemberIds())) {
            Set<Long> chatMemberIds = chat.getMembers().stream().map(ChatMember::getId).collect(Collectors.toSet());
            members.addAll(chatDTO.getMemberIds().stream()
                    .filter(memberId -> !chatMemberIds.contains(memberId))
                    .map(memberId -> {
                        ChatMember member = new ChatMember();
                        member.setChatId(chat.getId());
                        member.setMemberId(memberId);
                        return member;
                    }).collect(Collectors.toSet())
            );
        }
        chatRepository.save(chat);
    }

    @Override
    @Transactional
    public void upload(MultipartFile file, Long entityId, EntityTypeEnum entityType, String message, List<Long> memberIds) {
        ChatDTO chatDTO = new ChatDTO();
        chatDTO.setEntityId(entityId);
        chatDTO.setEntityType(entityType);
        chatDTO.setMessage(message);
        chatDTO.setMemberIds(memberIds);
        chatDTO.setAttachmentId(fileUploadService.upload(file, entityType, entityId, DocumentType.CHAT_ATTACHMENT).getId());
        this.create(chatDTO);
    }

    @Override
    public List<EmployeeListLoginDTO> getEmployeeList(EmployeeFilter filter) {
        filter.setRoles(Role.getCompanyAndCabinetRoleCodes());
        filter.setSize(Integer.MAX_VALUE);
        filter.setHasLogin(true);
        return employeeRepository.getResultList(filter).getList().stream().map(employee -> {
            EmployeeListLoginDTO employeeListLoginDTO = new EmployeeListLoginDTO();
            employeeListLoginDTO.setId(employee.getId());
            employeeListLoginDTO.setName(employee.getName());
            employeeListLoginDTO.setLogin(employee.getLogin());
            return employeeListLoginDTO;
        }).toList();
    }

    @Override
    public Page<ChatListAdminDTO> getListForAdmin(BaseFilter filter) {
        ResultList<ChatItem> resultList = chatItemRepository.getResultList(filter);
        List<ChatListAdminDTO> result = resultList.getList().stream().map(chatItem -> {
            ChatListAdminDTO chatListAdminDTO = new ChatListAdminDTO();
            chatListAdminDTO.setId(chatItem.getId());
            chatListAdminDTO.setMessage(chatItem.getMessage());
            chatListAdminDTO.setAttachment(Optional.ofNullable(chatItem.getAttachmentId()).map(attachmentId -> chatItem.getAttachment().toFileDTO()).orElse(null));
            chatListAdminDTO.setCreator(chatItem.getCreator().toCommonDTO());
            chatListAdminDTO.setCreatedDate(chatItem.getCreatedDate());
            return chatListAdminDTO;
        }).toList();
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    private Long getRequestCreatorId(Long entityId, EntityTypeEnum entityType) {
        return switch (entityType) {
            case LEAVE_REQUEST -> leaveRequestRepository.findById(entityId)
                    .map(LeaveRequest::getCreatorId)
                    .orElseThrow(() -> validationService.entityNotFoundExceptionThrow(LeaveRequest.class.getSimpleName()).get());
            case BENEFIT_REQUEST -> benefitRequestRepository.findById(entityId)
                    .map(BenefitRequest::getCreatorId)
                    .orElseThrow(() -> validationService.entityNotFoundExceptionThrow(BenefitRequest.class.getSimpleName()).get());
            case EQUIPMENT_REQUEST -> equipmentRequestRepository.findById(entityId)
                    .map(EquipmentRequest::getCreatorId)
                    .orElseThrow(() -> validationService.entityNotFoundExceptionThrow(EquipmentRequest.class.getSimpleName()).get());
            case EVENT_REQUEST -> eventRequestRepository.findById(entityId)
                    .map(EventRequest::getCreatorId)
                    .orElseThrow(() -> validationService.entityNotFoundExceptionThrow(EventRequest.class.getSimpleName()).get());
            default -> throw new IllegalStateException("Unexpected Entity Type: " + entityType);
        };
    }
}
