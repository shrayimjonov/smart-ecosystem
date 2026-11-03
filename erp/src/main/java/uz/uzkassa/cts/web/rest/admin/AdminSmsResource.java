package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.sms.EtcRequestDTO;
import uz.uzkassa.cts.service.SmsService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminSmsRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminSmsResource {

    SmsService smsService;

    @PostMapping(ApiConstants.send)
    public ResponseEntity<Void> send(@RequestBody EtcRequestDTO smsSendDTO) {
        smsService.send(smsSendDTO);
        return ResponseEntity.ok().build();
    }
}
