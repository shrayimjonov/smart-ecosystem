package uz.uzkassa.cts.jobs;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.payload.AttendancePayload;
import uz.uzkassa.cts.dto.payload.Payload;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqAttendanceProducer;
import uz.uzkassa.cts.repository.EmployeeRepository;
import uz.uzkassa.cts.repository.HolidayRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.time.LocalDate;
import java.util.List;

@Component
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Profile({"dev", "prod"})
public class AttendanceCronJob {

    RabbitMqAttendanceProducer rabbitMqAttendanceProducer;
    EmployeeRepository employeeRepository;
    HolidayRepository holidayRepository;

    @Scheduled(cron = "0 0 1 1 * ?")
    public void create() {
        List<Long> holidayIds = holidayRepository.getAllHolidayIdByRange(DateUtil.getMonthFirstDate(LocalDate.now()), DateUtil.getMonthLastDate(LocalDate.now()));
        employeeRepository.findAllByDeletedIsFalse().forEach(employee -> rabbitMqAttendanceProducer.create(new AttendancePayload(employee.getId(), holidayIds)));
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void updateItem() {
        employeeRepository.findAllByDeletedIsFalse().forEach(employee -> rabbitMqAttendanceProducer.updateItem(new Payload(employee.getId())));
    }
}
