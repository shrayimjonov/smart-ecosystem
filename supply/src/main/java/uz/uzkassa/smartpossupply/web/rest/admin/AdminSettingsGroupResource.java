package uz.uzkassa.smartpossupply.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.genericsettings.SettingsGroupAssignDTO;
import uz.uzkassa.smartpossupply.dto.genericsettings.SettingsGroupDTO;
import uz.uzkassa.smartpossupply.dto.genericsettings.SettingsGroupLookUpDTO;
import uz.uzkassa.smartpossupply.service.SettingsGroupService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminSettingsGroupRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminSettingsGroupResource {

    SettingsGroupService settingsGroupService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody SettingsGroupDTO settingsGroupDTO) {
        return ResponseEntity.ok(settingsGroupService.create(settingsGroupDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody SettingsGroupDTO settingsGroupDTO) {
        return ResponseEntity.ok(settingsGroupService.update(id, settingsGroupDTO));
    }

    @PutMapping("/assign-group")
    public ResponseEntity<Void> assignGroup(@RequestBody SettingsGroupAssignDTO settingsGroupAssignDTO) {
        settingsGroupService.assign(settingsGroupAssignDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<SettingsGroupLookUpDTO>> lookUp() {
        return ResponseEntity.ok(settingsGroupService.lookUp());
    }

}
