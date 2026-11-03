package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.mailing.ScheduleMessage;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.schedulemessage.ScheduleMessageDTO;
import uz.uzkassa.smartpossupply.dto.schedulemessage.ScheduleMessageDetailDTO;
import uz.uzkassa.smartpossupply.dto.schedulemessage.ScheduleMessageListDTO;
import uz.uzkassa.smartpossupply.enums.ScheduleMessageStatus;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.repository.MailingListRepository;
import uz.uzkassa.smartpossupply.repository.ScheduleMessageRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ScheduleMessageService extends BaseService {

    MailingListRepository mailingListRepository;
    ScheduleMessageRepository scheduleMessageRepository;
    ScheduleMessageJobService scheduleMessageJobService;

    @Transactional
    public Long create(ScheduleMessageDTO scheduleMessageDTO) {
        ScheduleMessage scheduleMessage = new ScheduleMessage();
        scheduleMessage.setSubjectUz(scheduleMessageDTO.getSubjectUz());
        scheduleMessage.setSubjectRu(scheduleMessageDTO.getSubjectRu());
        scheduleMessage.setSubjectCyrillic(scheduleMessageDTO.getSubjectCyrillic());
        scheduleMessage.setMessageUz(scheduleMessageDTO.getMessageUz());
        scheduleMessage.setMessageRU(scheduleMessageDTO.getMessageRu());
        scheduleMessage.setMessageCyrillic(scheduleMessageDTO.getMessageCyrillic());
        scheduleMessage.setScheduleDate(scheduleMessageDTO.getScheduleDate());
        scheduleMessage.setChannelType(scheduleMessageDTO.getChannelType());
        scheduleMessage.setAppCategory(scheduleMessageDTO.getAppCategory());
        scheduleMessage.setMailingList(mailingListRepository.getReferenceById(scheduleMessageDTO.getMailingListId()));
        final String jobId = UUID.randomUUID().toString();
        scheduleMessage.setJobId(jobId);
        scheduleMessageRepository.save(scheduleMessage);
        scheduleMessageJobService.createJob(jobId, scheduleMessage.getId(), scheduleMessage.getScheduleDate());
        return scheduleMessage.getId();
    }

    @Transactional
    public Long update(Long id, ScheduleMessageDTO scheduleMessageDTO) {
        scheduleMessageDTO.setId(id);
        return scheduleMessageRepository.findById(id)
                .map(scheduleMessage -> {
                    if (ScheduleMessageStatus.SENT == scheduleMessage.getStatus()) {
                        throw validationService.badRequestExceptionThrow("You cannot update a scheduled message with the status sent").get();
                    }
                    boolean modifiedDate = !scheduleMessage.getScheduleDate().equals(scheduleMessageDTO.getScheduleDate());
                    scheduleMessage.setSubjectUz(scheduleMessageDTO.getSubjectUz());
                    scheduleMessage.setSubjectRu(scheduleMessageDTO.getSubjectRu());
                    scheduleMessage.setSubjectCyrillic(scheduleMessageDTO.getSubjectCyrillic());
                    scheduleMessage.setMessageUz(scheduleMessageDTO.getMessageUz());
                    scheduleMessage.setMessageRU(scheduleMessageDTO.getMessageRu());
                    scheduleMessage.setMessageCyrillic(scheduleMessageDTO.getMessageCyrillic());
                    scheduleMessage.setAppCategory(scheduleMessageDTO.getAppCategory());
                    scheduleMessage.setScheduleDate(scheduleMessageDTO.getScheduleDate());
                    scheduleMessage.setChannelType(scheduleMessageDTO.getChannelType());
                    scheduleMessageRepository.save(scheduleMessage);
                    if (modifiedDate) {
                        if (scheduleMessageJobService.getJob(scheduleMessage.getJobId()) != null) {
                            scheduleMessageJobService.updateJob(scheduleMessage.getJobId(), scheduleMessageDTO.getScheduleDate());
                        } else {
                            final String jobId = UUID.randomUUID().toString();
                            scheduleMessageJobService.createJob(jobId, scheduleMessage.getId(), scheduleMessageDTO.getScheduleDate());
                            scheduleMessage.setJobId(jobId);
                            scheduleMessageRepository.save(scheduleMessage);
                        }
                    }
                    return id;
                }).orElseThrow(validationService.entityNotFoundExceptionThrow(ScheduleMessage.class.getSimpleName(), "id", id));
    }

    public ScheduleMessageDetailDTO get(Long id) {
        return scheduleMessageRepository.findById(id)
                .map(scheduleMessage -> {
                    ScheduleMessageDetailDTO scheduleMessageDetailDTO = new ScheduleMessageDetailDTO();
                    scheduleMessageDetailDTO.setId(scheduleMessage.getId());
                    scheduleMessageDetailDTO.setSubjectUz(scheduleMessage.getSubjectUz());
                    scheduleMessageDetailDTO.setSubjectRu(scheduleMessage.getSubjectRu());
                    scheduleMessageDetailDTO.setSubjectCyrillic(scheduleMessage.getSubjectCyrillic());
                    scheduleMessageDetailDTO.setMessageUz(scheduleMessage.getMessageUz());
                    scheduleMessageDetailDTO.setMessageRu(scheduleMessage.getMessageRu());
                    scheduleMessageDetailDTO.setAppCategory(scheduleMessage.getAppCategory());
                    scheduleMessageDetailDTO.setMessageCyrillic(scheduleMessage.getMessageCyrillic());
                    scheduleMessageDetailDTO.setScheduleDate(scheduleMessage.getScheduleDate());
                    scheduleMessageDetailDTO.setStatus(scheduleMessage.getStatus());
                    scheduleMessageDetailDTO.setChannelType(scheduleMessage.getChannelType());
                    scheduleMessageDetailDTO.setMailingList(scheduleMessage.getMailingList().toCommonDTO());
                    return scheduleMessageDetailDTO;
                })
                .orElseThrow(validationService.entityNotFoundExceptionThrow(ScheduleMessage.class.getSimpleName(), "id", id));
    }

    @Transactional
    public void updateStatus(Long id, ScheduleMessageStatus status) {
        scheduleMessageRepository.findById(id).ifPresentOrElse(scheduleMessage -> {
            if (ScheduleMessageStatus.SENT != scheduleMessage.getStatus()) {
                if (ScheduleMessageStatus.ACTIVE == status && scheduleMessage.getScheduleDate().isBefore(LocalDateTime.now())) {
                    throw validationService.badRequestExceptionThrow("Edit the scheduled date, the date cannot be in the past").get();
                }
                scheduleMessage.setStatus(status);
                scheduleMessageRepository.save(scheduleMessage);
                if (ScheduleMessageStatus.IN_ACTIVE == scheduleMessage.getStatus()
                        && scheduleMessageJobService.getJob(scheduleMessage.getJobId()) != null) {
                    scheduleMessageJobService.deleteJob(scheduleMessage.getJobId());
                }
            }
        }, () -> {
            throw validationService.entityNotFoundExceptionThrow(ScheduleMessage.class.getSimpleName(), "id", id).get();
        });
    }

    public Page<ScheduleMessageListDTO> getList(BaseFilter filter) {
        ResultList<ScheduleMessage> resultList = scheduleMessageRepository.getResultList(filter);
        List<ScheduleMessageListDTO> result = resultList
                .getList()
                .stream()
                .map(scheduleMessage -> {
                    ScheduleMessageListDTO scheduleMessageListDTO = new ScheduleMessageListDTO();
                    scheduleMessageListDTO.setId(scheduleMessage.getId());
                    scheduleMessageListDTO.setSubjectUz(scheduleMessage.getSubjectUz());
                    scheduleMessageListDTO.setSubjectRu(scheduleMessage.getSubjectRu());
                    scheduleMessageListDTO.setSubjectCyrillic(scheduleMessage.getSubjectCyrillic());
                    scheduleMessageListDTO.setMessageUz(scheduleMessage.getMessageUz());
                    scheduleMessageListDTO.setMessageRu(scheduleMessage.getMessageRu());
                    scheduleMessageListDTO.setMessageCyrillic(scheduleMessage.getMessageCyrillic());
                    scheduleMessageListDTO.setScheduleDate(scheduleMessage.getScheduleDate());
                    scheduleMessageListDTO.setStatus(scheduleMessage.getStatus());
                    return scheduleMessageListDTO;
                })
                .toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }


    @Transactional
    @Override
    public void deleteById(Long id) {
        scheduleMessageRepository.findById(id).ifPresentOrElse(scheduleMessage -> {
            scheduleMessageRepository.deleteById(id);
            if (scheduleMessageJobService.getJob(scheduleMessage.getJobId()) != null) {
                scheduleMessageJobService.deleteJob(scheduleMessage.getJobId());
            }
        }, () -> {
            throw validationService.entityNotFoundExceptionThrow(ScheduleMessage.class.getSimpleName(), "id", id).get();
        });
    }
}
