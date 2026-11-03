package uz.uzkassa.smartpossupply.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.genericsettings.AdminGenericSettingsListDTO;
import uz.uzkassa.smartpossupply.dto.genericsettings.GenericSettingsBatchDTO;
import uz.uzkassa.smartpossupply.enums.GenericSettingsFieldType;
import uz.uzkassa.smartpossupply.enums.GenericSettingsType;
import uz.uzkassa.smartpossupply.enums.WidgetType;
import uz.uzkassa.smartpossupply.service.GenericSettingsService;

import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 07.02.2024
 */
@RestController
@RequestMapping(ApiConstants.adminGenericSettingsRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminGenericSettingsResource {

    GenericSettingsService genericSettingsService;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody GenericSettingsBatchDTO settings) {
        genericSettingsService.create(settings);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<AdminGenericSettingsListDTO>> getList() {
        return ResponseEntity.ok(genericSettingsService.getListForAdmin());
    }

    @GetMapping(ApiConstants.types)
    public ResponseEntity<EnumSet<GenericSettingsType>> getTypes() {
        return ResponseEntity.ok(EnumSet.allOf(GenericSettingsType.class));
    }

    @GetMapping("/field-types")
    public ResponseEntity<EnumSet<GenericSettingsFieldType>> getFieldTypes() {
        return ResponseEntity.ok(EnumSet.allOf(GenericSettingsFieldType.class));
    }

    @GetMapping("/widget-types")
    public ResponseEntity<EnumSet<WidgetType>> getWidgetTypes() {
        return ResponseEntity.ok(EnumSet.allOf(WidgetType.class));
    }
}
