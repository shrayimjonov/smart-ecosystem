package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.base.CommonCodeDTO;
import uz.uzkassa.cts.dto.template.TemplateDTO;
import uz.uzkassa.cts.dto.template.admin.TemplateDetailAdminDTO;
import uz.uzkassa.cts.dto.template.admin.TemplateListAdminDTO;
import uz.uzkassa.cts.enums.ChannelType;
import uz.uzkassa.cts.enums.TemplateType;
import uz.uzkassa.cts.filter.TemplateFilter;
import uz.uzkassa.cts.service.TemplateService;

import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminTemplateRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminTemplateResource {

    TemplateService templateService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody TemplateDTO templateDTO) {
        return ResponseEntity.ok(templateService.create(templateDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody TemplateDTO templateDTO) {
        return ResponseEntity.ok(templateService.update(id, templateDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<TemplateDetailAdminDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(templateService.getForAdmin(id));
    }

    @GetMapping
    public ResponseEntity<Page<TemplateListAdminDTO>> getList(@ParameterObject TemplateFilter filter) {
        return ResponseEntity.ok(templateService.getListForAdmin(filter));
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonCodeDTO>> lookUp(@ParameterObject TemplateFilter filter) {
        return ResponseEntity.ok(templateService.lookUp(filter));
    }

    @GetMapping(ApiConstants.types)
    public ResponseEntity<EnumSet<TemplateType>> getTypes() {
        return ResponseEntity.ok(EnumSet.allOf(TemplateType.class));
    }

    @Deprecated
    //@See /api/admin/v1/commons/channel-types
    @GetMapping("/channel-types")
    public ResponseEntity<EnumSet<ChannelType>> getChanelTypes() {
        return ResponseEntity.ok(EnumSet.allOf(ChannelType.class));
    }
}
