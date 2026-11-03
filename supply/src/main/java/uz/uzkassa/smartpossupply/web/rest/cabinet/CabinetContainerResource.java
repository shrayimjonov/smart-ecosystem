package uz.uzkassa.smartpossupply.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.container.ContainerDTO;
import uz.uzkassa.smartpossupply.dto.container.ContainerDetailDTO;
import uz.uzkassa.smartpossupply.dto.container.ContainerListDTO;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.service.ContainerService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetContainerRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetContainerResource {

    ContainerService containerService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody ContainerDTO containerDTO) {
        return ResponseEntity.ok(containerService.create(containerDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody ContainerDTO containerDTO) {
        return ResponseEntity.ok(containerService.update(id, containerDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<ContainerDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(containerService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<ContainerListDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(containerService.getList(filter));
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        containerService.delete(id);
        return ResponseEntity.ok().build();
    }
}
