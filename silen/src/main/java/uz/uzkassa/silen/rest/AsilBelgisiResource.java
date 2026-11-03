package uz.uzkassa.silen.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.silen.dto.CheckCisesDTO;
import uz.uzkassa.silen.integration.asilbelgi.dto.AuthKey;
import uz.uzkassa.silen.integration.asilbelgi.dto.CisInfoResponse;
import uz.uzkassa.silen.integration.asilbelgi.dto.ReportInfo;
import uz.uzkassa.silen.integration.asilbelgi.dto.TrueApiRequest;
import uz.uzkassa.silen.service.AsilBelgiService;

import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/true")
@RequiredArgsConstructor
public class AsilBelgisiResource {
    private final AsilBelgiService asilBelgiService;

    @GetMapping("/auth-key")
    public ResponseEntity<AuthKey> getGuid() {
        return ResponseEntity.ok(asilBelgiService.getAuthKey());
    }

    @PostMapping("/sign-in")
    public ResponseEntity<String> signIn(@RequestBody @Valid final AuthKey key) {
        return ResponseEntity.ok(asilBelgiService.signIn(key));
    }

    @PutMapping("/update-token")
    public ResponseEntity<String> updateTrueToken(@RequestBody final AuthKey data) {
        return ResponseEntity.ok(asilBelgiService.updateTrueToken(data));
    }

    @PutMapping("/refresh-tokens")
    public ResponseEntity<Integer> refreshTokenOnce() {
        return ResponseEntity.ok(asilBelgiService.refreshTokensOnce());
    }

    @PostMapping("/cises-info")
    public ResponseEntity<CisInfoResponse[]> cisesInfo(@RequestBody final CheckCisesDTO request) {
        return ResponseEntity.ok(asilBelgiService.cisesInfo(request));
    }

    @GetMapping("/aggregated-list")
    public ResponseEntity<Set<String>> aggregatedList(@ModelAttribute final TrueApiRequest request) {
        final Set<String> data = asilBelgiService.aggregatedList(request);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/report-info/{organizationId}/{id}")
    public ResponseEntity<ReportInfo> getReportInfo(@PathVariable final String organizationId, @PathVariable final String id) {
        return ResponseEntity.ok(asilBelgiService.getReportInfo(organizationId, id));
    }

    @GetMapping("/history/{code}")
    public ResponseEntity<Object> getReportInfo(@PathVariable final String code) {
        return ResponseEntity.ok(asilBelgiService.getCisesHistory(code));
    }

    @GetMapping("/balance")
    public ResponseEntity<Object> getProductGroupBalance() {
        return ResponseEntity.ok(asilBelgiService.getProductGroupBalance());
    }

    @GetMapping("/doc/info/{id}")
    public ResponseEntity<Object> getDocInfo(@PathVariable final String id) {
        return ResponseEntity.ok(asilBelgiService.getDocInfo(id));
    }
}
