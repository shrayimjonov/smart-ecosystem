package uz.uzkassa.smartpos.web.rest;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpos.dto.ResponseData;
import uz.uzkassa.smartpos.dto.TerminalDTO;
import uz.uzkassa.smartpos.dto.TerminalLogDTO;
import uz.uzkassa.smartpos.dto.filter.management.TerminalFilter;
import uz.uzkassa.smartpos.dto.filter.management.TerminalLogFilter;
import uz.uzkassa.smartpos.integration.cto.CtoResultDTO;
import uz.uzkassa.smartpos.service.TerminalService;

import java.util.List;
import java.util.Set;

import static uz.uzkassa.smartpos.security.AuthoritiesConstants.SMARTPOS_ADMIN;

@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class TerminalResource {
    private final TerminalService terminalService;

    @GetMapping("/terminal")
    public Page<TerminalDTO> getList(@ParameterObject TerminalFilter filter) {
        return terminalService.getList(filter, false);
    }

    @GetMapping("/terminal/check")
    @ResponseBody
    public ResponseEntity<Boolean> checkTerminal(@ParameterObject TerminalDTO dto) {
        return ResponseEntity.ok().body(terminalService.checkTerminal(dto));
    }

    @GetMapping("/terminal/choose")
    @ResponseBody
    public ResponseData<List<TerminalDTO>> choose(@RequestParam(required = false) Set<Long> branchIds, @RequestParam(required = false) Set<Long> branchGroupIds, @RequestParam(required = false) Long branchId) {
        return ResponseData.ok(terminalService.choose(branchIds, branchGroupIds, branchId));
    }

    @GetMapping("/terminal/sync")
    public ResponseEntity<Void> syncTerminal(@RequestParam("id") Long terminalId) {
        terminalService.syncTerminal(terminalId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/terminal/webhook")
    @Secured(SMARTPOS_ADMIN)
    public ResponseEntity<Void> terminalWebhook(@RequestBody @Valid CtoResultDTO dto) {
        terminalService.syncTerminal(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/terminal/log")
    public boolean checkTerminalLogRequired(@RequestHeader(name = "TERMINAL_SN") String serialNumber) {
        return terminalService.checkTerminalLogRequired(serialNumber);
    }

    @PutMapping(value = "/terminal/log", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updateTerminalLog(@RequestParam(required = false) MultipartFile dbFile, @RequestHeader(name = "TERMINAL_SN") String serialNumber) {
        terminalService.updateTerminalLog(serialNumber, dbFile);
    }

    @GetMapping("/terminal/log/list")
    public ResponseEntity<Page<TerminalLogDTO>> getAllTerminalLog(@ParameterObject TerminalLogFilter filter) {
        return ResponseEntity.ok(terminalService.getTerminalLog(filter));
    }
}
