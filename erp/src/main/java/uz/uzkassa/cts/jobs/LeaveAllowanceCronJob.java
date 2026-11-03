package uz.uzkassa.cts.jobs;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.payload.Payload;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqLeaveAllowanceProducer;
import uz.uzkassa.cts.repository.EmployeeRepository;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Profile("prod")
public class LeaveAllowanceCronJob {

    RabbitMqLeaveAllowanceProducer rabbitMqLeaveAllowanceProducer;
    EmployeeRepository employeeRepository;

    @Scheduled(cron = "0 0 4 1 * ?") //Fire at every year first day at 04:00 AM
    public void create() {
        employeeRepository.findAllByDeletedIsFalse().forEach(employee -> rabbitMqLeaveAllowanceProducer.create(new Payload(employee.getId())));
    }
}
