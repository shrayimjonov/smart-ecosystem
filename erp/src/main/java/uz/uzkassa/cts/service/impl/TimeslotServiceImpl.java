package uz.uzkassa.cts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.domain.timeslot.Timeslot;
import uz.uzkassa.cts.domain.timeslot.TimeslotItem;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.timeslot.TimeslotDTO;
import uz.uzkassa.cts.dto.timeslot.TimeslotItemDTO;
import uz.uzkassa.cts.dto.timeslot.admin.TimeslotListAdminDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.TimeslotFilter;
import uz.uzkassa.cts.repository.TimeslotItemRepository;
import uz.uzkassa.cts.repository.TimeslotRepository;
import uz.uzkassa.cts.service.TimeslotService;
import uz.uzkassa.cts.utils.DateUtil;
import uz.uzkassa.cts.validator.ValidationContext;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
@Slf4j
public class TimeslotServiceImpl extends BaseServiceImpl<Timeslot, Long> implements TimeslotService {

    private final TimeslotRepository timeslotRepository;
    private final TimeslotItemRepository timeslotItemRepository;
    private final ValidationContext validationContext;

    public TimeslotServiceImpl(TimeslotRepository timeslotRepository, TimeslotItemRepository timeslotItemRepository, ValidationContext validationContext) {
        super(timeslotRepository);
        this.timeslotRepository = timeslotRepository;
        this.timeslotItemRepository = timeslotItemRepository;
        this.validationContext = validationContext;
    }

    @Override
    @Transactional
    public Long create(TimeslotDTO timeslotDTO) {
        validationContext.validate(timeslotDTO);
        Timeslot timeslot = new Timeslot();
        timeslot.setName(timeslotDTO.getName());
        timeslot.setType(timeslotDTO.getType());
        timeslot.setWorkingHour(timeslotDTO.getWorkingHour());
        timeslot.setLunchHour(timeslotDTO.getLunchHour());
        timeslot.setEntityId(timeslotDTO.getEntityId());
        timeslot.setEntityType(timeslotDTO.getEntityType());
        if (StringUtils.isNotEmpty(timeslotDTO.getFromTime())) {
            timeslot.setFromTime(LocalTime.of(Integer.parseInt(timeslotDTO.getFromTime().split(":")[0]), Integer.parseInt(timeslotDTO.getFromTime().split(":")[1])));
        }
        if (StringUtils.isNotEmpty(timeslotDTO.getToTime())) {
            timeslot.setToTime(LocalTime.of(Integer.parseInt(timeslotDTO.getToTime().split(":")[0]), Integer.parseInt(timeslotDTO.getToTime().split(":")[1])));
        }
        timeslotRepository.save(timeslot);
        if (!CollectionUtils.isEmpty(timeslotDTO.getItems())) {
            timeslotDTO.getItems().forEach(timeslotItemDTO -> {
                TimeslotItem timeslotItem = new TimeslotItem();
                timeslotItem.setDay(DayOfWeek.of(timeslotItemDTO.getDay()));
                if (StringUtils.isNotEmpty(timeslotItemDTO.getFromTime())) {
                    timeslotItem.setFromTime(LocalTime.of(Integer.parseInt(timeslotItemDTO.getFromTime().split(":")[0]), Integer.parseInt(timeslotItemDTO.getFromTime().split(":")[1])));
                }
                if (StringUtils.isNotEmpty(timeslotItemDTO.getToTime())) {
                    timeslotItem.setToTime(LocalTime.of(Integer.parseInt(timeslotItemDTO.getToTime().split(":")[0]), Integer.parseInt(timeslotItemDTO.getToTime().split(":")[1])));
                }
                if (StringUtils.isNotEmpty(timeslotItemDTO.getLunchFromTime())) {
                    timeslotItem.setLunchFromTime(LocalTime.of(Integer.parseInt(timeslotItemDTO.getLunchFromTime().split(":")[0]), Integer.parseInt(timeslotItemDTO.getLunchFromTime().split(":")[1])));
                }
                if (StringUtils.isNotEmpty(timeslotItemDTO.getLunchToTime())) {
                    timeslotItem.setLunchToTime(LocalTime.of(Integer.parseInt(timeslotItemDTO.getLunchToTime().split(":")[0]), Integer.parseInt(timeslotItemDTO.getLunchToTime().split(":")[1])));
                }
                timeslot.addItem(timeslotItem);
            });
        }
        return timeslot.getId();
    }

    @Override
    @Transactional
    public Long update(Long id, TimeslotDTO timeslotDTO) {
        timeslotDTO.setId(id);
        validationContext.validate(timeslotDTO);
        return timeslotRepository.findById(id)
                .map(timeslot -> {
                    timeslot.setName(timeslotDTO.getName());
                    timeslot.setType(timeslotDTO.getType());
                    timeslot.setWorkingHour(timeslotDTO.getWorkingHour());
                    timeslot.setLunchHour(timeslotDTO.getLunchHour());
                    if (StringUtils.isNotEmpty(timeslotDTO.getFromTime())) {
                        timeslot.setFromTime(LocalTime.of(Integer.parseInt(timeslotDTO.getFromTime().split(":")[0]), Integer.parseInt(timeslotDTO.getFromTime().split(":")[1])));
                    }
                    if (StringUtils.isNotEmpty(timeslotDTO.getToTime())) {
                        timeslot.setToTime(LocalTime.of(Integer.parseInt(timeslotDTO.getToTime().split(":")[0]), Integer.parseInt(timeslotDTO.getToTime().split(":")[1])));
                    }

                    Map<Long, TimeslotItem> timeslotItemsMap = timeslot
                            .getItems()
                            .stream()
                            .collect(Collectors.toMap(TimeslotItem::getId, Function.identity()));

                    if (!CollectionUtils.isEmpty(timeslotDTO.getItems())) {
                        for (TimeslotItemDTO timeslotItemDTO : timeslotDTO.getItems()) {
                            TimeslotItem timeslotItem;
                            if (timeslotItemDTO.getId() != null && timeslotItemsMap.get(timeslotItemDTO.getId()) != null) {
                                timeslotItem = timeslotItemsMap.get(timeslotItemDTO.getId());
                            } else {
                                timeslotItem = new TimeslotItem();
                            }
                            timeslotItem.setDay(DayOfWeek.of(timeslotItemDTO.getDay()));
                            if (StringUtils.isNotEmpty(timeslotItemDTO.getFromTime())) {
                                timeslotItem.setFromTime(LocalTime.of(Integer.parseInt(timeslotItemDTO.getFromTime().split(":")[0]), Integer.parseInt(timeslotItemDTO.getFromTime().split(":")[1])));
                            }
                            if (StringUtils.isNotEmpty(timeslotItemDTO.getToTime())) {
                                timeslotItem.setToTime(LocalTime.of(Integer.parseInt(timeslotItemDTO.getToTime().split(":")[0]), Integer.parseInt(timeslotItemDTO.getToTime().split(":")[1])));
                            }
                            if (StringUtils.isNotEmpty(timeslotItemDTO.getLunchFromTime())) {
                                timeslotItem.setLunchFromTime(LocalTime.of(Integer.parseInt(timeslotItemDTO.getLunchFromTime().split(":")[0]), Integer.parseInt(timeslotItemDTO.getLunchFromTime().split(":")[1])));
                            }
                            if (StringUtils.isNotEmpty(timeslotItemDTO.getLunchToTime())) {
                                timeslotItem.setLunchToTime(LocalTime.of(Integer.parseInt(timeslotItemDTO.getLunchToTime().split(":")[0]), Integer.parseInt(timeslotItemDTO.getLunchToTime().split(":")[1])));
                            }
                            timeslot.addItem(timeslotItem);
                            if (timeslotItem.getId() != null) {
                                timeslotItemsMap.remove(timeslotItem.getId());
                            }
                        }
                    }
                    if (!timeslotItemsMap.isEmpty()) {
                        timeslotItemRepository.deleteAllByIdInBatch(timeslotItemsMap.keySet());
                    }
                    timeslotRepository.save(timeslot);
                    return timeslot.getId();
                }).orElseThrow(validationService.entityNotFoundExceptionThrow(TIMESLOT));
    }

    @Override
    public TimeslotDTO getRoundTheClock() {
        return timeslotRepository.findFirstByRoundTheClockIsTrueAndDeletedIsFalse().map(this::getDetail).orElseThrow(validationService.entityNotFoundExceptionThrow("Round the Clock Timeslot is not found"));
    }

    @Override
    public Page<TimeslotListAdminDTO> getListForAdmin(TimeslotFilter filter) {
        ResultList<Timeslot> resultList = timeslotRepository.getResultList(filter);
        List<TimeslotListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(timeslot -> {
                    TimeslotListAdminDTO timeslotListDTO = new TimeslotListAdminDTO();
                    timeslotListDTO.setId(timeslot.getId());
                    timeslotListDTO.setName(timeslot.getName());
                    timeslotListDTO.setType(timeslot.getType());
                    timeslotListDTO.setStatus(timeslot.getStatus());
                    timeslotListDTO.setCreator(timeslot.getCreator().toCommonDTO());
                    timeslotListDTO.setCreatedDate(timeslot.getCreatedDate());
                    return timeslotListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public List<CommonDTO> lookUp(TimeslotFilter filter) {
        filter.setStatus(CommonStatus.ACTIVE.getCode());
        return timeslotRepository.getResultList(filter).getList().stream().map(Timeslot::toCommonDTO).collect(Collectors.toList());
    }

    @Override
    public TimeslotDTO getDetail(Timeslot timeslot) {
        TimeslotDTO timeslotDTO = new TimeslotDTO();
        timeslotDTO.setId(timeslot.getId());
        timeslotDTO.setName(timeslot.getName());
        timeslotDTO.setType(timeslot.getType());
        timeslotDTO.setWorkingHour(timeslot.getWorkingHour());
        timeslotDTO.setLunchHour(timeslot.getLunchHour());
        timeslotDTO.setEntityId(timeslot.getEntityId());
        timeslotDTO.setEntityType(timeslot.getEntityType());
        if (timeslot.getFromTime() != null) {
            timeslotDTO.setFromTime(DateUtil.TIME_FORMATTER.format(timeslot.getFromTime()));
        }
        if (timeslot.getToTime() != null) {
            timeslotDTO.setToTime(DateUtil.TIME_FORMATTER.format(timeslot.getToTime()));
        }
        timeslotDTO.setItems(timeslot.getItems()
                .stream()
                .map(timeslotItem -> {
                    TimeslotItemDTO timeslotItemDTO = new TimeslotItemDTO();
                    timeslotItemDTO.setId(timeslotItem.getId());
                    timeslotItemDTO.setDay(timeslotItem.getDay().getValue());
                    if (timeslotItem.getFromTime() != null) {
                        timeslotItemDTO.setFromTime(DateUtil.TIME_FORMATTER.format(timeslotItem.getFromTime()));
                    }
                    if (timeslotItem.getToTime() != null) {
                        timeslotItemDTO.setToTime(DateUtil.TIME_FORMATTER.format(timeslotItem.getToTime()));
                    }
                    if (timeslotItem.getLunchFromTime() != null) {
                        timeslotItemDTO.setLunchFromTime(DateUtil.TIME_FORMATTER.format(timeslotItem.getLunchFromTime()));
                    }
                    if (timeslotItem.getLunchToTime() != null) {
                        timeslotItemDTO.setLunchToTime(DateUtil.TIME_FORMATTER.format(timeslotItem.getLunchToTime()));
                    }
                    return timeslotItemDTO;
                }).collect(Collectors.toList()));
        return timeslotDTO;
    }

    @Override
    public TimeslotDTO get(Long id) {
        return timeslotRepository.findById(id).map(this::getDetail).orElseThrow(validationService.entityNotFoundExceptionThrow(TIMESLOT));
    }

    @Override
    public TimeslotDTO getByEntityIdAndEntityType(Long entityId, EntityTypeEnum entityType) {
        return timeslotRepository.findFirstByEntityIdAndEntityType(entityId, entityType).map(this::getDetail).orElseThrow(validationService.entityNotFoundExceptionThrow(TIMESLOT));
    }

    @Override
    public List<CommonDTO> getItems() {
        return timeslotRepository.findAllByEntityIdIsNullAndEntityTypeIsNullAndDeletedIsFalse().stream().map(SimpleEntity::toCommonDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateStatus(Long id, CommonStatus status) {
        timeslotRepository
                .findById(id)
                .ifPresentOrElse(timeslot -> {
                    timeslot.setStatus(status);
                    timeslotRepository.save(timeslot);
                }, () -> validationService.entityNotFoundExceptionThrow(TIMESLOT).get());
    }
}
