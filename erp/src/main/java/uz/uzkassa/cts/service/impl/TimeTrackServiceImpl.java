package uz.uzkassa.cts.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.hrms.TimeTrack;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.employee.EmployeeListPhotoDTO;
import uz.uzkassa.cts.dto.report.TimeTrackReportDTO;
import uz.uzkassa.cts.dto.timetrack.CameraTimeTrackDTO;
import uz.uzkassa.cts.dto.timetrack.admin.TimeTrackListAdminDTO;
import uz.uzkassa.cts.dto.timetrack.admin.TimeTrackListByEmployeeAdminDTO;
import uz.uzkassa.cts.enums.Role;
import uz.uzkassa.cts.enums.TimeTrackType;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.EmployeeFilter;
import uz.uzkassa.cts.filter.TimeTrackReportFilter;
import uz.uzkassa.cts.repository.EmployeeRepository;
import uz.uzkassa.cts.repository.TimeTrackRepository;
import uz.uzkassa.cts.service.TimeTrackService;
import uz.uzkassa.cts.utils.DateUtil;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.List;

@Transactional(readOnly = true)
@Service
public class TimeTrackServiceImpl extends BaseServiceImpl<TimeTrack, Long> implements TimeTrackService {

    private final TimeTrackRepository timeTrackRepository;
    private final EmployeeRepository employeeRepository;
    private final ValidationContext validationContext;

    public TimeTrackServiceImpl(TimeTrackRepository timeTrackRepository, EmployeeRepository employeeRepository, ValidationContext validationContext) {
        super(timeTrackRepository);
        this.timeTrackRepository = timeTrackRepository;
        this.employeeRepository = employeeRepository;
        this.validationContext = validationContext;
    }

    @Override
    @Transactional
    public Long create(CameraTimeTrackDTO cameraTimeTrackDTO) {
        validationContext.validate(cameraTimeTrackDTO);
        TimeTrack timeTrack = new TimeTrack();
        timeTrack.setEmployeeId(cameraTimeTrackDTO.getUserId());
        timeTrack.setImageUrl(cameraTimeTrackDTO.getImageUrl());
        timeTrack.setThumbnailUrl(cameraTimeTrackDTO.getThumbnailUrl());
        timeTrack.setVideoUrl(cameraTimeTrackDTO.getVideoUrl());
        timeTrack.setEventExternalId(cameraTimeTrackDTO.getEventExternalId());
        timeTrack.setCameraIp(cameraTimeTrackDTO.getCameraIp());
        if (TimeTrackType.IN == TimeTrackType.valueOf(cameraTimeTrackDTO.getType())) {
            timeTrack.setCheckIn(DateUtil.convertToLocalTimeZone(cameraTimeTrackDTO.getDateTime()));
        } else {
            timeTrack.setCheckOut(DateUtil.convertToLocalTimeZone(cameraTimeTrackDTO.getDateTime()));
        }
        return timeTrackRepository.save(timeTrack).getId();
    }

    @Override
    @Transactional
    public Long createManually(CameraTimeTrackDTO cameraTimeTrackDTO) {
        validationContext.validate(cameraTimeTrackDTO);
        TimeTrack timeTrack = new TimeTrack();
        timeTrack.setEmployeeId(cameraTimeTrackDTO.getUserId());
        if (TimeTrackType.IN == TimeTrackType.valueOf(cameraTimeTrackDTO.getType())) {
            timeTrack.setCheckIn(DateUtil.convertToLocalTimeZone(cameraTimeTrackDTO.getDateTime()));
        } else {
            timeTrack.setCheckOut(DateUtil.convertToLocalTimeZone(cameraTimeTrackDTO.getDateTime()));
        }
        return timeTrackRepository.save(timeTrack).getId();
    }

    @Override
    @Transactional
    public Long update(Long id, CameraTimeTrackDTO cameraTimeTrackDTO) {
        validationContext.validate(cameraTimeTrackDTO);
        return timeTrackRepository.findByIdAndEmployeeIdAndDeletedIsFalse(id, cameraTimeTrackDTO.getUserId()).map(timeTrack -> {
            if (TimeTrackType.IN == TimeTrackType.valueOf(cameraTimeTrackDTO.getType()) && timeTrack.getCheckIn() != null) {
                timeTrack.setCheckIn(DateUtil.convertToLocalTimeZone(cameraTimeTrackDTO.getDateTime()));
            } else if (TimeTrackType.OUT == TimeTrackType.valueOf(cameraTimeTrackDTO.getType()) && timeTrack.getCheckOut() != null) {
                timeTrack.setCheckOut(DateUtil.convertToLocalTimeZone(cameraTimeTrackDTO.getDateTime()));
            } else {
                throw validationService.entityNotFoundExceptionThrow(TIME_TRACK, TYPE, cameraTimeTrackDTO.getType()).get();
            }
            return timeTrackRepository.save(timeTrack).getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(TIME_TRACK));
    }

    @Override
    public Page<TimeTrackListAdminDTO> getListForAdmin(BaseFilter filter) {
        ResultList<TimeTrack> resultList = timeTrackRepository.getResultList(filter);
        List<TimeTrackListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(timeTrack -> {
                    TimeTrackListAdminDTO timeTrackListDTO = new TimeTrackListAdminDTO();
                    timeTrackListDTO.setId(timeTrack.getId());
                    timeTrackListDTO.setEmployee(timeTrack.getEmployee().toCommonDTO());
                    timeTrackListDTO.setDepartment(timeTrack.getEmployee().getDepartment() != null ? timeTrack.getEmployee().getDepartment().toCommonDTO() : null);
                    timeTrackListDTO.setPosition(timeTrack.getEmployee().getPosition() != null ? timeTrack.getEmployee().getPosition().toCommonDTO() : null);
                    timeTrackListDTO.setCheckIn(timeTrack.getCheckIn());
                    timeTrackListDTO.setCheckOut(timeTrack.getCheckOut());
                    timeTrackListDTO.setImageUrl(timeTrack.getImageUrl());
                    timeTrackListDTO.setThumbnailUrl(timeTrack.getThumbnailUrl());
                    timeTrackListDTO.setVideoUrl(timeTrack.getVideoUrl());
                    return timeTrackListDTO;
                }).toList();
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public Page<TimeTrackListByEmployeeAdminDTO> getListByEmployeeForAdmin(BaseFilter filter) {
        ResultList<TimeTrack> resultList = timeTrackRepository.getResultList(filter);
        List<TimeTrackListByEmployeeAdminDTO> result = resultList
                .getList()
                .stream()
                .map(timeTrack -> {
                    TimeTrackListByEmployeeAdminDTO timeTrackListDTO = new TimeTrackListByEmployeeAdminDTO();
                    timeTrackListDTO.setId(timeTrack.getId());
                    timeTrackListDTO.setCheckIn(timeTrack.getCheckIn());
                    timeTrackListDTO.setCheckOut(timeTrack.getCheckOut());
                    timeTrackListDTO.setImageUrl(timeTrack.getImageUrl());
                    timeTrackListDTO.setThumbnailUrl(timeTrack.getThumbnailUrl());
                    timeTrackListDTO.setVideoUrl(timeTrack.getVideoUrl());
                    return timeTrackListDTO;
                }).toList();
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public List<EmployeeListPhotoDTO> getEmployeeList() {
        EmployeeFilter filter = new EmployeeFilter();
        filter.setRoles(Role.getCompanyAndCabinetRoleCodes());
        filter.setSize(Integer.MAX_VALUE);
        filter.setHasTimeTrackPhoto(true);
        return employeeRepository
                .getResultList(filter)
                .getList()
                .stream()
                .map(employee -> {
                    EmployeeListPhotoDTO employeeListPhotoDTO = new EmployeeListPhotoDTO();
                    employeeListPhotoDTO.setId(employee.getId());
                    employeeListPhotoDTO.setFirstName(employee.getFirstName());
                    employeeListPhotoDTO.setLastName(employee.getLastName());
                    employeeListPhotoDTO.setPatronymic(employeeListPhotoDTO.getPatronymic());
                    employeeListPhotoDTO.setPhoto(employee.getTimeTrackPhoto().toFileDTO());
                    return employeeListPhotoDTO;
                }).toList();
    }

    @Override
    public Page<TimeTrackReportDTO> getReport(TimeTrackReportFilter filter) {
        ResultList<TimeTrackReportDTO> resultList = timeTrackRepository.getReport(filter);
        return new PageImpl<>(resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }
}
