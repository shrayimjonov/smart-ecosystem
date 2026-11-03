package uz.uzkassa.cts.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.Holiday;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.holiday.HolidayDTO;
import uz.uzkassa.cts.dto.holiday.HolidayDetailDTO;
import uz.uzkassa.cts.dto.holiday.HolidayListDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.HolidayRepository;
import uz.uzkassa.cts.service.HolidayService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class HolidayServiceImpl extends BaseServiceImpl<Holiday, Long> implements HolidayService {

    HolidayRepository holidayRepository;
    ValidationContext validationContext;

    public HolidayServiceImpl(HolidayRepository holidayRepository, ValidationContext validationContext) {
        super(holidayRepository);
        this.holidayRepository = holidayRepository;
        this.validationContext = validationContext;
    }

    @Override
    @Transactional
    public Long create(HolidayDTO holidayDTO) {
        validationContext.validate(holidayDTO);
        Holiday holiday = new Holiday();
        holiday.setName(holidayDTO.getName());
        holiday.setDescription(holidayDTO.getDescription());
        holiday.setHolidayDate(holidayDTO.getHolidayDate());
        holiday.setFromDate(holidayDTO.getFromDate());
        holiday.setToDate(holidayDTO.getToDate());
        holiday.setDayOff(holidayDTO.isDayOff());
        holiday.setRecurring(holidayDTO.isRecurring());
        holiday.setCompanyId(holidayDTO.getCompanyId());
        return holidayRepository.save(holiday).getId();
    }

    @Override
    @Transactional
    public Long update(Long id, HolidayDTO holidayDTO) {
        return holidayRepository.findById(id)
                .map(holiday -> {
                    validationContext.validate(holidayDTO);
                    holidayDTO.setId(id);
                    holiday.setName(holidayDTO.getName());
                    holiday.setDescription(holidayDTO.getDescription());
                    holiday.setHolidayDate(holidayDTO.getHolidayDate());
                    holiday.setFromDate(holidayDTO.getFromDate());
                    holiday.setToDate(holidayDTO.getToDate());
                    holiday.setDayOff(holidayDTO.isDayOff());
                    holiday.setRecurring(holidayDTO.isRecurring());
                    holiday.setCompanyId(holidayDTO.getCompanyId());
                    holidayRepository.save(holiday);
                    return id;
                }).orElseThrow(() -> validationService.entityNotFoundExceptionThrow(HOLIDAY).get());
    }

    @Override
    public HolidayDetailDTO get(Long id) {
        return holidayRepository.findById(id)
                .map(holiday -> {
                    HolidayDetailDTO holidayDetailDTO = new HolidayDetailDTO();
                    holidayDetailDTO.setId(holiday.getId());
                    holidayDetailDTO.setName(holiday.getName());
                    holidayDetailDTO.setDescription(holiday.getDescription());
                    holidayDetailDTO.setHolidayDate(holiday.getHolidayDate());
                    holidayDetailDTO.setFromDate(holiday.getFromDate());
                    holidayDetailDTO.setToDate(holiday.getToDate());
                    holidayDetailDTO.setDayOff(holiday.isDayOff());
                    holidayDetailDTO.setCreator(holiday.getCreator().toCommonDTO());
                    holidayDetailDTO.setCompany(holiday.getCompany().toCommonDTO());
                    holidayDetailDTO.setCreatedDate(holiday.getCreatedDate());
                    holidayDetailDTO.setStatus(holiday.getStatus());
                    holidayDetailDTO.setRecurring(holiday.isRecurring());
                    return holidayDetailDTO;
                }).orElseThrow(() -> validationService.entityNotFoundExceptionThrow(HOLIDAY).get());
    }

    @Override
    public Page<HolidayListDTO> getList(BaseFilter filter) {
        ResultList<Holiday> resultList = holidayRepository.getResultList(filter);
        List<HolidayListDTO> result = resultList.getList()
                .stream()
                .map(holiday -> {
                    HolidayListDTO holidayListDTO = new HolidayListDTO();
                    holidayListDTO.setId(holiday.getId());
                    holidayListDTO.setName(holiday.getName());
                    holidayListDTO.setHolidayDate(holiday.getHolidayDate());
                    holidayListDTO.setFromDate(holiday.getFromDate());
                    holidayListDTO.setToDate(holiday.getToDate());
                    holidayListDTO.setDayOff(holiday.isDayOff());
                    holidayListDTO.setCompany(holiday.getCompany().toCommonDTO());
                    holidayListDTO.setStatus(holiday.getStatus());
                    holidayListDTO.setRecurring(holiday.isRecurring());
                    return holidayListDTO;
                }).toList();
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    @Transactional
    public void updateStatus(Long id, CommonStatus status) {
        holidayRepository.findById(id)
                .ifPresentOrElse(holiday -> {
                    holiday.setStatus(status);
                    holidayRepository.save(holiday);
                }, () -> validationService.entityNotFoundExceptionThrow(HOLIDAY).get());
    }

    @Override
    @Transactional
    public void createRecurring() {
        List<Holiday> holidays = holidayRepository.findAllByYear(LocalDate.now().minusYears(1).getYear())
                .stream().map(holidayLastYear -> {
                    Holiday holiday = new Holiday();
                    holiday.setName(holidayLastYear.getName());
                    holiday.setDescription(holidayLastYear.getDescription());
                    holiday.setHolidayDate(holidayLastYear.getHolidayDate().plusYears(1));
                    holiday.setDayOff(holidayLastYear.isDayOff());
                    holiday.setRecurring(holidayLastYear.isRecurring());
                    holiday.setCompanyId(holidayLastYear.getCompanyId());
                    return holiday;
                }).toList();
        holidayRepository.saveAll(holidays);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        holidayRepository.deleteById(id);
    }
}
