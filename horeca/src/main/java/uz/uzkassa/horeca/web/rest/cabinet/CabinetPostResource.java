package uz.uzkassa.horeca.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.post.PostDTO;
import uz.uzkassa.horeca.dto.post.PostDetailDTO;
import uz.uzkassa.horeca.dto.post.PostListDTO;
import uz.uzkassa.horeca.enums.AttachmentType;
import uz.uzkassa.horeca.enums.PostStatus;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.service.PostService;

import java.util.EnumSet;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetPostRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetPostResource {

    PostService postService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody PostDTO postDTO) {
        return ResponseEntity.ok(postService.create(postDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<PostDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(postService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<PostListDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(postService.getList(filter));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody PostDTO postDTO) {
        return ResponseEntity.ok(postService.update(id, postDTO));
    }

    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<PostStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(PostStatus.class));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") PostStatus status) {
        postService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/attachment-types")
    public ResponseEntity<EnumSet<AttachmentType>> getAttachmentTypes() {
        return ResponseEntity.ok(EnumSet.allOf(AttachmentType.class));
    }
}
