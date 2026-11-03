package uz.uzkassa.smartpossupply.web.rest.admin;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.post.PostDTO;
import uz.uzkassa.smartpossupply.dto.post.PostDetailDTO;
import uz.uzkassa.smartpossupply.dto.post.PostListDTO;
import uz.uzkassa.smartpossupply.enums.AttachmentType;
import uz.uzkassa.smartpossupply.enums.PostStatus;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.service.PostService;

import java.util.EnumSet;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 02.11.2023
 */
@RestController
@RequestMapping(ApiConstants.adminPostRootApi)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminPostResource {

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
        return ResponseEntity.ok(EnumSet.of(PostStatus.PUBLISHED, PostStatus.IN_MODERATION));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") String status) {
        postService.updateStatus(id, PostStatus.valueOf(status));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/attachment-types")
    public ResponseEntity<EnumSet<AttachmentType>> getAttachmentTypes() {
        return ResponseEntity.ok(EnumSet.allOf(AttachmentType.class));
    }

    @PostMapping("/upload")
    public ResponseEntity<List<Long>> uploadPhoto(@RequestPart List<MultipartFile> files) {
        return ResponseEntity.ok(postService.upload(files));
    }
}
