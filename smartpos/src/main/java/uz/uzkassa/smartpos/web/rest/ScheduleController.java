package uz.uzkassa.smartpos.web.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpos.dto.receipt.ScheduleDTO;
import uz.uzkassa.smartpos.dto.receipt.ScheduleRequestDTO;
import uz.uzkassa.smartpos.dto.scheduling.request.ScheduleRequest;
import uz.uzkassa.smartpos.dto.scheduling.response.ScheduleResponse;
import uz.uzkassa.smartpos.service.SchedulerService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schedule")
public class ScheduleController {
    private final SchedulerService schedulerService;

    @PostMapping("/v1")
    public ScheduleDTO oldSchedule(@RequestBody @Valid ScheduleRequestDTO request) {
        return schedulerService.schedule(request, true);
    }

    @PostMapping("/dynamic")
    public ScheduleResponse receiptSchedule(@RequestBody @Valid ScheduleRequest request) {
        return schedulerService.schedule(request);
    }
}
