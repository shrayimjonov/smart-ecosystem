package uz.uzkassa.smartpos.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.TerminalLogToKafkaDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/terminals/logs")
@RequiredArgsConstructor
public class TerminalLogResource {
    @Value("${spring.kafka.terminal-log-topic}")
    private String terminalLogTopic;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @PostMapping
    public void sendLogsToKafka(@RequestHeader("TERMINAL_SN") String terminalSN, @RequestBody @NotEmpty List<TerminalLogToKafkaDto> terminalLogs) {
        terminalLogs.forEach(terminalLogDto -> {
            terminalLogDto.setSerialNumber(terminalSN);
            try {
                kafkaTemplate.send(terminalLogTopic, objectMapper.writeValueAsString(terminalLogDto));
            } catch (Exception e) {
                log.error("Error sending log to Kafka topic", e);
            }
        });
    }
}
