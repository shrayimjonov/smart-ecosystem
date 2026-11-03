package uz.uzkassa.cts.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.note.NoteDTO;
import uz.uzkassa.cts.dto.note.cabinet.NoteDetailCabinetDTO;
import uz.uzkassa.cts.dto.note.cabinet.NoteListCabinetDTO;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.NoteService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetNoteRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetNoteResource {

    NoteService noteService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody NoteDTO noteDTO) {
        return ResponseEntity.ok(noteService.create(noteDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody NoteDTO noteDTO) {
        return ResponseEntity.ok(noteService.update(id, noteDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<NoteDetailCabinetDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(noteService.getForCabinet(id));
    }

    @GetMapping
    public ResponseEntity<Page<NoteListCabinetDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(noteService.getListForCabinet(filter));
    }

    @PostMapping(value = ApiConstants.upload, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> upload(@RequestParam("file") MultipartFile file, @RequestParam EntityTypeEnum entityType, @RequestParam Long entityId) {
        return ResponseEntity.ok(noteService.upload(entityType, entityId, file));
    }
}
