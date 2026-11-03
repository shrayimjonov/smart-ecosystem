package uz.uzkassa.smartpos.web.rest.terminal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpos.integration.meningUyim.MeningUyimAccount;
import uz.uzkassa.smartpos.integration.meningUyim.MeningUyimClient;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/terminals/mening-uyim")
public class TerminalMeningUyimResource {
    private final MeningUyimClient client;

    @GetMapping("/find-account")
    public ResponseEntity<MeningUyimAccount> findAccount(@RequestParam(name = "code") final String code) {
        return ResponseEntity.ok(client.findAccoount(code));
    }
}
