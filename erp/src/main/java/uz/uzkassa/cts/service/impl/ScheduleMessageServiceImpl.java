package uz.uzkassa.cts.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.ScheduleMessage;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.schedulemessage.ScheduleMessageDTO;
import uz.uzkassa.cts.dto.schedulemessage.ScheduleMessageDetailDTO;
import uz.uzkassa.cts.dto.schedulemessage.ScheduleMessageListDTO;
import uz.uzkassa.cts.enums.ScheduleMessageStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.ScheduleMessageRepository;
import uz.uzkassa.cts.service.ScheduleMessageJobService;
import uz.uzkassa.cts.service.ScheduleMessageService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ScheduleMessageServiceImpl extends BaseServiceImpl<ScheduleMessage, Long> implements ScheduleMessageService {

    private final ScheduleMessageRepository scheduleMessageRepository;
    private final ScheduleMessageJobService scheduleMessageJobService;
    private final ValidationContext validationContext;

    public ScheduleMessageServiceImpl(ScheduleMessageRepository scheduleMessageRepository,
                                      ScheduleMessageJobService scheduleMessageJobService, ValidationContext validationContext) {
        super(scheduleMessageRepository);
        this.scheduleMessageRepository = scheduleMessageRepository;
        this.scheduleMessageJobService = scheduleMessageJobService;
        this.validationContext = validationContext;
    }

    @Override
    @Transactional
    public Long create(ScheduleMessageDTO scheduleMessageDTO) {
        validationContext.validate(scheduleMessageDTO);
        ScheduleMessage scheduleMessage = new ScheduleMessage();
        scheduleMessage.setSubject(scheduleMessageDTO.getSubject());
        scheduleMessage.setMessage(scheduleMessageDTO.getMessage());
        scheduleMessage.setScheduleDate(scheduleMessageDTO.getScheduleDate());
        scheduleMessage.setChannelType(scheduleMessageDTO.getChannelType());
        scheduleMessage.setMailingListId(scheduleMessageDTO.getMailingListId());
        final String jobId = UUID.randomUUID().toString();
        scheduleMessage.setJobId(jobId);
        scheduleMessageRepository.save(scheduleMessage);
        scheduleMessageJobService.createJob(jobId, scheduleMessage.getId(), scheduleMessage.getScheduleDate());
        return scheduleMessage.getId();
    }

    @Override
    @Transactional
    public Long update(Long id, ScheduleMessageDTO scheduleMessageDTO) {
        scheduleMessageDTO.setId(id);
        validationContext.validate(scheduleMessageDTO);
        return scheduleMessageRepository.findById(id)
                .map(scheduleMessage -> {
                    if (ScheduleMessageStatus.SENT == scheduleMessage.getStatus()) {
                        throw validationService.badRequestExceptionThrow(CANNOT_UPDATE_SENT_SCHEDULED_MESSAGE).get();
                    }
                    boolean modifiedDate = !scheduleMessage.getScheduleDate().equals(scheduleMessageDTO.getScheduleDate());
                    scheduleMessage.setSubject(scheduleMessageDTO.getSubject());
                    scheduleMessage.setMessage(scheduleMessageDTO.getMessage());
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
                }).orElseThrow(validationService.entityNotFoundExceptionThrow(SCHEDULE));
    }

    @Override
    public ScheduleMessageDetailDTO get(Long id) {
        return scheduleMessageRepository.findById(id)
                .map(scheduleMessage -> {
                    ScheduleMessageDetailDTO scheduleMessageDetailDTO = new ScheduleMessageDetailDTO();
                    scheduleMessageDetailDTO.setId(scheduleMessage.getId());
                    scheduleMessageDetailDTO.setMessage(scheduleMessage.getMessage());
                    scheduleMessageDetailDTO.setSubject(scheduleMessage.getSubject());
                    scheduleMessageDetailDTO.setScheduleDate(scheduleMessage.getScheduleDate());
                    scheduleMessageDetailDTO.setChannelType(scheduleMessage.getChannelType());
                    scheduleMessageDetailDTO.setStatus(scheduleMessage.getStatus());
                    return scheduleMessageDetailDTO;
                })
                .orElseThrow(validationService.entityNotFoundExceptionThrow(SCHEDULE));
    }

    @Override
    @Transactional
    public void updateStatus(Long id, ScheduleMessageStatus status) {
        scheduleMessageRepository.findById(id).ifPresentOrElse(scheduleMessage -> {
            if (ScheduleMessageStatus.SENT != scheduleMessage.getStatus()) {
                if (ScheduleMessageStatus.ACTIVE == status && scheduleMessage.getScheduleDate().isBefore(LocalDateTime.now())) {
                    throw validationService.badRequestExceptionThrow(SCHEDULE_DATE_CANNOT_BE_IN_THE_PAST).get();
                }
                scheduleMessage.setStatus(status);
                scheduleMessageRepository.save(scheduleMessage);
                if (ScheduleMessageStatus.IN_ACTIVE == scheduleMessage.getStatus()
                        && scheduleMessageJobService.getJob(scheduleMessage.getJobId()) != null) {
                    scheduleMessageJobService.deleteJob(scheduleMessage.getJobId());
                }
            }
        }, () -> validationService.entityNotFoundExceptionThrow(SCHEDULE).get());
    }

    @Override
    public Page<ScheduleMessageListDTO> getList(BaseFilter filter) {
        ResultList<ScheduleMessage> resultList = scheduleMessageRepository.getResultList(filter);
        List<ScheduleMessageListDTO> result = resultList
                .getList()
                .stream()
                .map(scheduleMessage -> {
                    ScheduleMessageListDTO scheduleMessageListDTO = new ScheduleMessageListDTO();
                    scheduleMessageListDTO.setId(scheduleMessage.getId());
                    scheduleMessageListDTO.setSubject(scheduleMessage.getSubject());
                    scheduleMessageListDTO.setMessage(scheduleMessage.getMessage());
                    scheduleMessageListDTO.setScheduleDate(scheduleMessage.getScheduleDate());
                    scheduleMessageListDTO.setChannelType(scheduleMessage.getChannelType());
                    scheduleMessageListDTO.setStatus(scheduleMessage.getStatus());
                    return scheduleMessageListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }


    @Transactional
    public void deleteById(Long id) {
        scheduleMessageRepository.findById(id).ifPresentOrElse(scheduleMessage -> {
            scheduleMessageRepository.deleteById(id);
            if (scheduleMessageJobService.getJob(scheduleMessage.getJobId()) != null) {
                scheduleMessageJobService.deleteJob(scheduleMessage.getJobId());
            }
        }, () -> validationService.entityNotFoundExceptionThrow(SCHEDULE).get());
    }
}
