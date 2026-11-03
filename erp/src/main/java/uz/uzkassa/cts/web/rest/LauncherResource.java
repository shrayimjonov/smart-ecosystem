package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.dto.launcher.LauncherTerminalRequestDto;
import uz.uzkassa.cts.service.LauncherService;

@RestController
@RequestMapping("/api/launcher")
@Tag(name = "Launcher")
@Slf4j
public class LauncherResource {

    private final LauncherService service;

    public LauncherResource(LauncherService orderService) {
        this.service = orderService;
    }

    @Operation(summary ="auth")
    @GetMapping("auth")
    public ResponseEntity<String> auth() {
        service.authenticate();
        return ResponseEntity.ok().build();
    }

    @Operation(summary ="action")
    @PostMapping("action")
    public ResponseEntity<LauncherTerminalRequestDto> action(LauncherTerminalRequestDto dto) {
        service.action(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary ="action")
    @PostMapping("update-bank")
    public ResponseEntity<Void> updateBank(LauncherTerminalRequestDto dto) {
        service.updateBankType(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary ="action")
    @GetMapping("unblock")
    public ResponseEntity<Void> unblock() {
//        LauncherTerminalRequestDto unBlockList = new LauncherTerminalRequestDto();
//        unBlockList.setAction("UNBLOCK");
//        unBlockList.getTerminalNumbers().add("N500W218548");
//        unBlockList.getTerminalNumbers().add("N500W218867");
//        unBlockList.getTerminalNumbers().add("N500W214839");
//        unBlockList.getTerminalNumbers().add("N500W217762");
//        unBlockList.getTerminalNumbers().add("N5000103586");
//        unBlockList.getTerminalNumbers().add("N500W217768");
//        unBlockList.getTerminalNumbers().add("N500W217919");
//        unBlockList.getTerminalNumbers().add("N500W217147");
//        unBlockList.getTerminalNumbers().add("N500W218295");
//        unBlockList.getTerminalNumbers().add("N500W218640");
//        unBlockList.getTerminalNumbers().add("N500W216192");
//        unBlockList.getTerminalNumbers().add("N500W217178");
//        unBlockList.getTerminalNumbers().add("N5000102929");
//        unBlockList.getTerminalNumbers().add("N500W214889");
//        unBlockList.getTerminalNumbers().add("N500W213656");
//        unBlockList.getTerminalNumbers().add("N500W218297");
//        unBlockList.getTerminalNumbers().add("N500W218745");
//        unBlockList.getTerminalNumbers().add("N500W205830");
//        unBlockList.getTerminalNumbers().add("N500W205095");
//        unBlockList.getTerminalNumbers().add("N5000103302");
//        unBlockList.getTerminalNumbers().add("N500W216143");
//        unBlockList.getTerminalNumbers().add("N500W216490");
//        unBlockList.getTerminalNumbers().add("N500W213305");
//        unBlockList.getTerminalNumbers().add("N500W205989");
//        unBlockList.getTerminalNumbers().add("N500W215708");
//        unBlockList.getTerminalNumbers().add("N500W216135");
//        unBlockList.getTerminalNumbers().add("N500W216222");
//        unBlockList.getTerminalNumbers().add("N500W205869");
//        unBlockList.getTerminalNumbers().add("N500W205810");
//        unBlockList.getTerminalNumbers().add("N500W205811");
//        unBlockList.getTerminalNumbers().add("N500W213112");
//        unBlockList.getTerminalNumbers().add("N500W205493");
//        unBlockList.getTerminalNumbers().add("N500W205687");
//        unBlockList.getTerminalNumbers().add("N500W215746");
//        unBlockList.getTerminalNumbers().add("N500W206401");
//        unBlockList.getTerminalNumbers().add("N500W216214");
//        unBlockList.getTerminalNumbers().add("N500W205502");
//        unBlockList.getTerminalNumbers().add("N500W206399");
//        unBlockList.getTerminalNumbers().add("N500W206109");
//        unBlockList.getTerminalNumbers().add("N500W206199");
//        unBlockList.getTerminalNumbers().add("N500W206046");
//        unBlockList.getTerminalNumbers().add("N500W213227");
//        unBlockList.getTerminalNumbers().add("N500W214963");
//        unBlockList.getTerminalNumbers().add("N500W206785");
//        unBlockList.getTerminalNumbers().add("N500W206218");
//        unBlockList.getTerminalNumbers().add("N500W205100");
//        unBlockList.getTerminalNumbers().add("N500W206110");
//        unBlockList.getTerminalNumbers().add("N500W206213");
//        unBlockList.getTerminalNumbers().add("N500W205649");
//        unBlockList.getTerminalNumbers().add("N500W206579");
//        unBlockList.getTerminalNumbers().add("N500W206735");
//        unBlockList.getTerminalNumbers().add("N500W218869");
//        unBlockList.getTerminalNumbers().add("N500W205986");
//        unBlockList.getTerminalNumbers().add("N500W205736");
//        unBlockList.getTerminalNumbers().add("N500W217912");
//        unBlockList.getTerminalNumbers().add("N500W206421");
//        unBlockList.getTerminalNumbers().add("N500W218244");
//        unBlockList.getTerminalNumbers().add("N500W213711");
//        unBlockList.getTerminalNumbers().add("N5000102930");
//        unBlockList.getTerminalNumbers().add("N500W213712");
//        unBlockList.getTerminalNumbers().add("N500W205981");
//        unBlockList.getTerminalNumbers().add("N500W205342");
//        unBlockList.getTerminalNumbers().add("N500W214076");
//        unBlockList.getTerminalNumbers().add("N5000102956");
//        unBlockList.getTerminalNumbers().add("N500W218706");
//        unBlockList.getTerminalNumbers().add("N500W217117");
//        unBlockList.getTerminalNumbers().add("N500W206578");
//        unBlockList.getTerminalNumbers().add("N500W206571");
//        unBlockList.getTerminalNumbers().add("N500W207017");
//        unBlockList.getTerminalNumbers().add("N500W205132");
//        unBlockList.getTerminalNumbers().add("N500W205739");
//        unBlockList.getTerminalNumbers().add("N500W214276");
//        unBlockList.getTerminalNumbers().add("N500W213714");
//        unBlockList.getTerminalNumbers().add("N500W214423");
//        unBlockList.getTerminalNumbers().add("N500W216210");
//        unBlockList.getTerminalNumbers().add("N500W206112");
//        unBlockList.getTerminalNumbers().add("N500W213487");
//        unBlockList.getTerminalNumbers().add("N500W213224");
//        unBlockList.getTerminalNumbers().add("N500W216318");
//        unBlockList.getTerminalNumbers().add("N500W206750");
//        unBlockList.getTerminalNumbers().add("N500W205726");
//        unBlockList.getTerminalNumbers().add("N500W213153");
//        unBlockList.getTerminalNumbers().add("N500W213162");
//        unBlockList.getTerminalNumbers().add("N500W206219");
//        unBlockList.getTerminalNumbers().add("N500W218865");
//        unBlockList.getTerminalNumbers().add("N500W213306");
//        service.action(unBlockList);
        return ResponseEntity.ok().build();
    }


}
