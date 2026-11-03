package uz.uzkassa.smartpossupply.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.terminal.TerminalLogListDTO;
import uz.uzkassa.smartpossupply.dto.terminal.TerminalSerialNumberDTO;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.service.TerminalLogService;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.adminTerminalLogRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminTerminalLogResource {

    TerminalLogService terminalLogService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody TerminalSerialNumberDTO terminalSerialNumberDTO) {
        return ResponseEntity.ok(terminalLogService.create(terminalSerialNumberDTO));
    }

    @GetMapping
    public ResponseEntity<Page<TerminalLogListDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(terminalLogService.getList(filter));
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        terminalLogService.delete(id);
        return ResponseEntity.ok().build();
    }
}