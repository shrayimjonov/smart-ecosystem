package uz.uzkassa.smartpos.web.rest.v2;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.ResponseData;
import uz.uzkassa.smartpos.dto.TerminalDTO;
import uz.uzkassa.smartpos.integration.cto.CtoFiscalCheckDTO;
import uz.uzkassa.smartpos.service.TerminalService;

import java.util.List;

@RestController
@RequestMapping("/api/v2/terminal")
@Slf4j
@RequiredArgsConstructor
public class TerminalResourceV2 {
    private final TerminalService terminalService;

    @GetMapping("/cashbox/choose")
    @ResponseBody
    public ResponseData<List<TerminalDTO>> choose(@RequestParam(required = false) Long branchId) {
        return ResponseData.ok(terminalService.chooseCashbox(branchId));
    }

    @PostMapping("/fiscalization/check")
    public Object fiscalizationCheck(@RequestBody @Valid final CtoFiscalCheckDTO request) {
        return terminalService.checkFiscalizationFromCto(request);
    }

}
