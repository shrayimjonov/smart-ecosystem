package uz.uzkassa.smartpossupply.web.rest.terminal;

import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.constants.HeaderConstants;
import uz.uzkassa.smartpossupply.dto.terminal.TerminalLogKafkaDTO;
import uz.uzkassa.smartpossupply.dto.terminal.TerminalLogListDTO;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.service.TerminalLogService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.terminalLogRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class TerminalLogResource {

    TerminalLogService terminalLogService;

    @PostMapping
    public ResponseEntity<Void> sendToKafka(@RequestHeader(name = HeaderConstants.TERMINAL_SN) String serialNumber, @RequestBody @NotEmpty List<TerminalLogKafkaDTO> terminalLogs) {
        terminalLogService.sendLogsToKafka(serialNumber, terminalLogs);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.check)
    public ResponseEntity<Boolean> check(@RequestHeader(name = HeaderConstants.TERMINAL_SN) String serialNumber) {
        return ResponseEntity.ok(terminalLogService.check(serialNumber));
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> update(@RequestParam MultipartFile file, @RequestHeader(name = HeaderConstants.TERMINAL_SN) String serialNumber) {
        terminalLogService.update(serialNumber, file);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<TerminalLogListDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(terminalLogService.getList(filter));
    }
}
