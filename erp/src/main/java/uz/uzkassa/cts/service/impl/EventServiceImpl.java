package uz.uzkassa.cts.service.impl;

import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.cts.domain.hrms.Event;
import uz.uzkassa.cts.domain.hrms.EventShare;
import uz.uzkassa.cts.domain.hrms.Reminder;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.event.EventDTO;
import uz.uzkassa.cts.dto.event.admin.EventDetailAdminDTO;
import uz.uzkassa.cts.dto.event.admin.EventListAdminDTO;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.EventStatus;
import uz.uzkassa.cts.filter.EventFilter;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.service.EventReminderJobService;
import uz.uzkassa.cts.service.EventService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

/**
 * Created date time: 26.12.2024 17:19
 */

@Transactional(readOnly = true)
@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class EventServiceImpl extends BaseServiceImpl<Event, Long> implements EventService {

    EventRepository eventRepository;
    EventShareRepository eventShareRepository;
    ReminderRepository reminderRepository;
    EventRequestRepository eventRequestRepository;
    CompanyRepository companyRepository;
    EventReminderJobService eventReminderJobService;
    ValidationContext validationContext;

    public EventServiceImpl(EventRepository eventRepository, EventShareRepository eventShareRepository, ReminderRepository reminderRepository, EventRequestRepository eventRequestRepository, CompanyRepository companyRepository, EventReminderJobService eventReminderJobService, ValidationContext validationContext) {
        super(eventRepository);
        this.eventRepository = eventRepository;
        this.eventShareRepository = eventShareRepository;
        this.reminderRepository = reminderRepository;
        this.eventRequestRepository = eventRequestRepository;
        this.companyRepository = companyRepository;
        this.eventReminderJobService = eventReminderJobService;
        this.validationContext = validationContext;
    }

    @Override
    @Transactional
    public Long create(EventDTO eventDTO) {
        validationContext.validate(eventDTO);
        Event event = new Event();
        event.setSubject(eventDTO.getSubject());
        event.setDescription(eventDTO.getDescription());
        event.setFromDate(eventDTO.getFromDate());
        event.setToDate(eventDTO.getToDate());
        event.setGuest(eventDTO.getGuest());
        event.setEventRequestId(eventDTO.getEventRequestId());
        event.setCompanyId(eventDTO.getCompanyId());
        eventRepository.save(event);
        eventShareRepository.saveAll(eventDTO
                .getParticipantIds()
                .stream()
                .map(participantId -> {
                    EventShare eventShare = new EventShare();
                    eventShare.setEventId(event.getId());
                    eventShare.setParticipantId(participantId);
                    return eventShare;
                }).toList());
        if (!CollectionUtils.isEmpty(eventDTO.getReminders())) {
            List<Reminder> reminders = new ArrayList<>();
            eventDTO.getReminders().forEach(reminderDTO -> {
                String jobId = UUID.randomUUID().toString();
                Duration before = switch (reminderDTO.getCode()) {
                    case "15_MINUTES" -> Duration.ofMinutes(15L);
                    case "30_MINUTES" -> Duration.ofMinutes(30L);
                    case "45_MINUTES" -> Duration.ofMinutes(45L);
                    case "1_HOUR" -> Duration.ofHours(1L);
                    default -> throw new IllegalStateException("Unexpected reminder code value: " + reminderDTO.getCode());
                };
                eventReminderJobService.createJob(jobId, event.getId(), eventDTO.getFromDate().minus(before));
                Reminder reminder = new Reminder();
                reminder.setEntityId(event.getId());
                reminder.setEntityType(EntityTypeEnum.EVENT);
                reminder.setCode(reminderDTO.getCode());
                reminder.setType(reminderDTO.getType());
                reminder.setJobId(jobId);
                reminders.add(reminder);
            });
            reminderRepository.saveAll(reminders);
        }
        return event.getId();
    }

    @Override
    @Transactional
    public Long update(Long id, EventDTO eventDTO) {
        validationContext.validate(eventDTO);
        return eventRepository.findById(id).map(event -> {
            if (EventStatus.CLOSED == event.getStatus()) {
                throw validationService.badRequestExceptionThrow(CANNOT_UPDATE_CLOSED_EVENT).get();
            }
            event.setSubject(eventDTO.getSubject());
            event.setDescription(eventDTO.getDescription());
            event.setFromDate(eventDTO.getFromDate());
            event.setToDate(eventDTO.getToDate());
            event.setGuest(eventDTO.getGuest());
            if (!Objects.equals(event.getEventRequestId(), eventDTO.getEventRequestId())) {
                event.setEventRequest(eventRequestRepository.findById(eventDTO.getEventRequestId()).orElseThrow(validationService.entityNotFoundExceptionThrow(EVENT_REQUEST)));
                event.setEventRequestId(eventDTO.getEventRequestId());
            }
            if (!Objects.equals(event.getCompanyId(), eventDTO.getCompanyId())) {
                event.setCompany(companyRepository.findById(eventDTO.getCompanyId()).orElseThrow(validationService.entityNotFoundExceptionThrow(COMPANY)));
                event.setCompanyId(eventDTO.getCompanyId());
            }
            eventShareRepository.deleteAllByEventId(id);
            eventShareRepository.saveAll(eventDTO
                    .getParticipantIds()
                    .stream()
                    .map(participantId -> {
                        EventShare eventShare = new EventShare();
                        eventShare.setEventId(event.getId());
                        eventShare.setParticipantId(participantId);
                        return eventShare;
                    }).toList());
            eventReminderJobService.deleteJobs(reminderRepository.findAllByEntityIdAndEntityType(id, EntityTypeEnum.EVENT).stream().map(Reminder::getJobId).toList());
            reminderRepository.deleteAllByEntityIdAndEntityType(id, EntityTypeEnum.EVENT);
            if (!CollectionUtils.isEmpty(eventDTO.getReminders())) {
                List<Reminder> reminders = new ArrayList<>();
                eventDTO.getReminders().forEach(reminderDTO -> {
                    String jobId = UUID.randomUUID().toString();
                    Duration before = switch (reminderDTO.getCode()) {
                        case "15_MINUTES" -> Duration.ofMinutes(15L);
                        case "30_MINUTES" -> Duration.ofMinutes(30L);
                        case "45_MINUTES" -> Duration.ofMinutes(45L);
                        case "1_HOUR" -> Duration.ofHours(1L);
                        default -> throw new IllegalStateException("Unexpected reminder code value: " + reminderDTO.getCode());
                    };
                    eventReminderJobService.createJob(jobId, event.getId(), eventDTO.getFromDate().minus(before));
                    Reminder reminder = new Reminder();
                    reminder.setEntityId(event.getId());
                    reminder.setEntityType(EntityTypeEnum.EVENT);
                    reminder.setCode(reminderDTO.getCode());
                    reminder.setType(reminderDTO.getType());
                    reminder.setJobId(jobId);
                    reminders.add(reminder);
                });
                reminderRepository.saveAll(reminders);
            }
            return id;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(EVENT));
    }

    @Override
    public EventDetailAdminDTO getForAdmin(Long id) {
        return eventRepository.findById(id).map(event -> {
            EventDetailAdminDTO eventDetailAdminDTO = new EventDetailAdminDTO();
            eventDetailAdminDTO.setId(event.getId());
            eventDetailAdminDTO.setCompany(event.getCompany().toCommonDTO());
            eventDetailAdminDTO.setEventRequest(event.getEventRequest().toCommonDTO());
            eventDetailAdminDTO.setSubject(event.getSubject());
            eventDetailAdminDTO.setDescription(event.getDescription());
            eventDetailAdminDTO.setFromDate(event.getFromDate());
            eventDetailAdminDTO.setToDate(event.getToDate());
            eventDetailAdminDTO.setGuest(event.getGuest());
            eventDetailAdminDTO.setCreator(event.getCreator().toCommonDTO());
            eventDetailAdminDTO.setCreatedDate(event.getCreatedDate());
            eventDetailAdminDTO.setParticipants(event.getShares().stream().map(eventShare -> eventShare.getParticipant().toCommonDTO()).toList());
            return eventDetailAdminDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(EVENT));
    }

    @Override
    public Page<EventListAdminDTO> getListForAdmin(EventFilter filter) {
        ResultList<Event> resultList = eventRepository.getResultList(filter);
        List<EventListAdminDTO> result = resultList.getList().stream().map(event -> {
            EventListAdminDTO eventListAdminDTO = new EventListAdminDTO();
            eventListAdminDTO.setId(event.getId());
            eventListAdminDTO.setStatus(event.getStatus());
            eventListAdminDTO.setSubject(event.getSubject());
            eventListAdminDTO.setDescription(event.getDescription());
            eventListAdminDTO.setFromDate(event.getFromDate());
            eventListAdminDTO.setToDate(event.getToDate());
            eventListAdminDTO.setGuest(event.getGuest());
            eventListAdminDTO.setCreator(event.getCreator().toCommonDTO());
            eventListAdminDTO.setCreatedDate(event.getCreatedDate());
            return eventListAdminDTO;
        }).collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    @Transactional
    public void updateStatus(Long id, EventStatus status) {
        eventRepository.findById(id).ifPresentOrElse(event -> {
            if (EventStatus.CLOSED != event.getStatus()) {
                event.setStatus(status);
                eventRepository.save(event);
            }
        }, () -> validationService.entityNotFoundExceptionThrow(EVENT).get());
    }
}
