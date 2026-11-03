package uz.uzkassa.cts.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.Holiday;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.domain.hrms.*;
import uz.uzkassa.cts.domain.timeslot.Timeslot;
import uz.uzkassa.cts.domain.timeslot.TimeslotItem;
import uz.uzkassa.cts.dto.attendance.*;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.leaverequest.LeaveRequestAttendanceDTO;
import uz.uzkassa.cts.filter.AttendanceFilter;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.service.AttendanceService;
import uz.uzkassa.cts.utils.DateUtil;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created date time: 29.01.2025 12:19
 */

@Service
@Transactional(readOnly = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AttendanceServiceImpl extends BaseServiceImpl<Attendance, Long> implements AttendanceService {

    AttendanceRepository attendanceRepository;
    AttendanceItemRepository attendanceItemRepository;
    EmployeeRepository employeeRepository;
    HolidayRepository holidayRepository;
    LeaveRequestRepository leaveRequestRepository;
    TimeTrackRepository timeTrackRepository;

    public AttendanceServiceImpl(
            AttendanceRepository attendanceRepository,
            AttendanceItemRepository attendanceItemRepository,
            EmployeeRepository employeeRepository,
            HolidayRepository holidayRepository,
            LeaveRequestRepository leaveRequestRepository,
            TimeTrackRepository timeTrackRepository
    ) {
        super(attendanceRepository);
        this.attendanceRepository = attendanceRepository;
        this.attendanceItemRepository = attendanceItemRepository;
        this.employeeRepository = employeeRepository;
        this.holidayRepository = holidayRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.timeTrackRepository = timeTrackRepository;
    }

    @Override
    @Transactional
    public void create(Long employeeId, List<Long> holidayIds) {
        LocalDate today = LocalDate.now();
        List<Holiday> holidays = holidayRepository.findAllById(holidayIds);
        User employee = employeeRepository.findById(employeeId).orElseThrow(validationService.entityNotFoundExceptionThrow(EMPLOYEE));
        Timeslot timeslot = Optional.ofNullable(employee.getTimeslot()).orElseThrow(validationService.entityNotFoundExceptionThrow(TIMESLOT));

        Attendance attendance = new Attendance();
        attendance.setEmployeeId(employeeId);
        attendance.setAttendanceDate(today);
        attendanceRepository.save(attendance);

        List<TimeslotItem> timeslotItems = Optional.ofNullable(timeslot.getItems()).orElseThrow(validationService.entityNotFoundExceptionThrow(TIMESLOT_ITEM));
        List<AttendanceItem> attendanceItems = new ArrayList<>();
        int monthlyPlannedHours = 0, normalDays = 0;

        for (LocalDate date = today; date.isBefore(today.plusMonths(1)); date = date.plusDays(1)) {
            LocalDate finalDate = date;
            Long holidayId = holidays
                    .stream()
                    .filter(holiday -> finalDate.isEqual(holiday.getHolidayDate()) || (holiday.getFromDate() != null && holiday.getToDate() != null && !(finalDate.isBefore(holiday.getFromDate()) || finalDate.isAfter(holiday.getToDate()))))
                    .map(Holiday::getId)
                    .findFirst()
                    .orElse(null);
            TimeslotItem timeslotItem = timeslotItems.get(date.getDayOfWeek().ordinal());
            AttendanceItem attendanceItem = new AttendanceItem();
            attendanceItem.setAttendanceId(attendance.getId());
            attendanceItem.setAttendanceDay(date.getDayOfMonth());
            attendanceItem.setHolidayId(holidayId);
            if (holidayId == null && timeslotItem != null && timeslotItem.getFromTime() != null && timeslotItem.getToTime() != null && timeslotItem.getLunchFromTime() != null && timeslotItem.getLunchToTime() != null) {
                int planned = (int) Duration.between(timeslotItem.getFromTime(), timeslotItem.getToTime()).minus(Duration.between(timeslotItem.getLunchFromTime(), timeslotItem.getLunchToTime())).toMinutes();
                attendanceItem.setPlannedHours(planned);
                monthlyPlannedHours += planned;
                normalDays++;
            }
            attendanceItems.add(attendanceItem);
        }

        attendance.setMonthlyPlannedHours(monthlyPlannedHours);
        attendance.setNormalDays(normalDays);
        attendance.setMonthlyActualHours(0);
        attendance.setActualDays(0);
        attendance.setItems(attendanceItems);
        attendanceRepository.save(attendance);
    }

    @Override
    @Transactional
    public void updateItem(Long employeeId) {
        LocalDate day = LocalDate.now().minusDays(1);
        Attendance attendance = attendanceRepository.findByEmployeeIdAndAttendanceDate(employeeId, DateUtil.getMonthFirstDate(day)).orElseThrow(validationService.entityNotFoundExceptionThrow(ATTENDANCE));
        Long leaveRequestId = leaveRequestRepository.getLeaveRequestIdByDateAndCreatorId(day, employeeId);
        List<TimeTrack> timeTrackList = timeTrackRepository.findAllByDateAndEmployeeIdOrderById(day, employeeId);

        int totalMinutes = 0;
        LocalDateTime lastCheckIn = null;
        for (TimeTrack timeTrack : timeTrackList) {
            if (timeTrack.getCheckIn() != null) {
                lastCheckIn = timeTrack.getCheckIn();
            } else if (timeTrack.getCheckOut() != null && lastCheckIn != null) {
                totalMinutes += Duration.between(lastCheckIn, timeTrack.getCheckOut()).toMinutes();
                lastCheckIn = null;
            }
        }

        AttendanceItem attendanceItem = attendanceItemRepository.findByAttendanceIdAndAttendanceDay(attendance.getId(), day.getDayOfMonth()).orElseThrow(validationService.entityNotFoundExceptionThrow(ATTENDANCE_ITEM));
        attendanceItem.setLeaveRequestId(leaveRequestId);
        attendanceItem.setActualHours(totalMinutes != 0 ? totalMinutes : null);
        attendanceItemRepository.save(attendanceItem);

        if (totalMinutes != 0) {
            attendance.setMonthlyActualHours(attendance.getMonthlyActualHours() + totalMinutes);
            attendance.setActualDays(attendance.getActualDays() + 1);
            attendanceRepository.save(attendance);
        }
    }

    @Override
    public Page<AttendanceListAdminDTO> getListForAdmin(AttendanceFilter filter) {
        ResultList<Attendance> resultList = attendanceRepository.getResultList(filter);
        List<AttendanceListAdminDTO> result = resultList.getList().stream().map(attendance -> {
            AttendanceListAdminDTO attendanceListAdminDTO = new AttendanceListAdminDTO();
            attendanceListAdminDTO.setEmployee(attendance.getEmployee().toCommonDTO());
            attendanceListAdminDTO.setDepartment(Optional.ofNullable(attendance.getEmployee().getDepartment()).map(Department::toCommonDTO).orElse(null));
            attendanceListAdminDTO.setPosition(Optional.ofNullable(attendance.getEmployee().getPosition()).map(Position::toCommonDTO).orElse(null));
            attendanceListAdminDTO.setMonthlyPlannedHours(attendance.getMonthlyPlannedHours());
            attendanceListAdminDTO.setMonthlyActualHours(attendance.getMonthlyActualHours());
            attendanceListAdminDTO.setNormalDays(attendance.getNormalDays());
            attendanceListAdminDTO.setActualDays(attendance.getActualDays());
            attendance.getItems().forEach(attendanceItem -> {
                AttendanceItemDTO attendanceItemDTO = new AttendanceItemDTO();
                attendanceItemDTO.setId(attendanceItem.getId());
                attendanceItemDTO.setDay(attendanceItem.getAttendanceDay());
                attendanceItemDTO.setPlannedHours(attendanceItem.getPlannedHours());
                attendanceItemDTO.setActualHours(attendanceItem.getActualHours());
                attendanceItemDTO.setHoliday(Optional.ofNullable(attendanceItem.getHoliday()).map(Holiday::toCommonDTO).orElse(null));
                attendanceItemDTO.setLeaveRequest(Optional.ofNullable(attendanceItem.getLeaveRequest()).map(leaveRequest -> {
                    LeaveRequestAttendanceDTO leaveRequestAttendanceDTO = new LeaveRequestAttendanceDTO();
                    leaveRequestAttendanceDTO.setId(leaveRequest.getId());
                    leaveRequestAttendanceDTO.setName(leaveRequest.getName());
                    leaveRequestAttendanceDTO.setAbbreviation(leaveRequest.getReason().getAbbreviation());
                    return leaveRequestAttendanceDTO;
                }).orElse(null));
                attendanceListAdminDTO.addItem(attendanceItemDTO);
            });
            return attendanceListAdminDTO;
        }).toList();
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public Page<AttendanceListByDepartmentAdminDTO> getListByDepartmentForAdmin(AttendanceFilter filter) {
        Page<AttendanceListAdminDTO> listForAdmin = this.getListForAdmin(filter);
        List<AttendanceListByDepartmentAdminDTO> result = new ArrayList<>();
        listForAdmin.get()
                .filter(attendanceListAdminDTO -> attendanceListAdminDTO.getDepartment() != null)
                .collect(Collectors.groupingBy(attendanceListAdminDTO -> attendanceListAdminDTO.getDepartment().getId()))
                .values().forEach(attendanceListAdminDTOList -> {
                    AttendanceListByDepartmentAdminDTO attendanceListByDepartmentAdminDTO = new AttendanceListByDepartmentAdminDTO();
                    attendanceListByDepartmentAdminDTO.setDepartment(attendanceListAdminDTOList.getFirst().getDepartment());
                    attendanceListByDepartmentAdminDTO.setAttendances(attendanceListAdminDTOList.stream().map(attendanceListAdminDTO -> {
                        AttendanceByDepartmentDTO attendanceByDepartmentDTO = new AttendanceByDepartmentDTO();
                        attendanceByDepartmentDTO.setPosition(attendanceListAdminDTO.getPosition());
                        attendanceByDepartmentDTO.setEmployee(attendanceListAdminDTO.getEmployee());
                        attendanceByDepartmentDTO.setMonthlyPlannedHours(attendanceListAdminDTO.getMonthlyPlannedHours());
                        attendanceByDepartmentDTO.setMonthlyActualHours(attendanceListAdminDTO.getMonthlyActualHours());
                        attendanceByDepartmentDTO.setNormalDays(attendanceListAdminDTO.getNormalDays());
                        attendanceByDepartmentDTO.setActualDays(attendanceListAdminDTO.getActualDays());
                        attendanceByDepartmentDTO.setItems(attendanceListAdminDTO.getItems());
                        return attendanceByDepartmentDTO;
                    }).toList());
                    result.add(attendanceListByDepartmentAdminDTO);
                });
        return new PageImpl<>(result, filter.getOrderedPageable(), listForAdmin.getTotalElements());
    }

    @Override
    public Page<AttendanceListByPositionAdminDTO> getListByPositionForAdmin(AttendanceFilter filter) {
        Page<AttendanceListAdminDTO> listForAdmin = this.getListForAdmin(filter);
        List<AttendanceListByPositionAdminDTO> result = new ArrayList<>();
        listForAdmin.get()
                .filter(attendanceListAdminDTO -> attendanceListAdminDTO.getPosition() != null)
                .collect(Collectors.groupingBy(attendanceListAdminDTO -> attendanceListAdminDTO.getPosition().getId()))
                .values().forEach(attendanceListAdminDTOList -> {
                    AttendanceListByPositionAdminDTO attendanceListByPositionAdminDTO = new AttendanceListByPositionAdminDTO();
                    attendanceListByPositionAdminDTO.setPosition(attendanceListAdminDTOList.getFirst().getPosition());
                    attendanceListByPositionAdminDTO.setAttendances(attendanceListAdminDTOList.stream().map(attendanceListAdminDTO -> {
                        AttendanceByPositionDTO attendanceByPositionDTO = new AttendanceByPositionDTO();
                        attendanceByPositionDTO.setDepartment(attendanceListAdminDTO.getDepartment());
                        attendanceByPositionDTO.setEmployee(attendanceByPositionDTO.getEmployee());
                        attendanceByPositionDTO.setMonthlyPlannedHours(attendanceListAdminDTO.getMonthlyPlannedHours());
                        attendanceByPositionDTO.setMonthlyActualHours(attendanceListAdminDTO.getMonthlyActualHours());
                        attendanceByPositionDTO.setNormalDays(attendanceListAdminDTO.getNormalDays());
                        attendanceByPositionDTO.setActualDays(attendanceListAdminDTO.getActualDays());
                        attendanceByPositionDTO.setItems(attendanceListAdminDTO.getItems());
                        return attendanceByPositionDTO;
                    }).toList());
                    result.add(attendanceListByPositionAdminDTO);
                });
        return new PageImpl<>(result, filter.getOrderedPageable(), listForAdmin.getTotalElements());
    }
}
