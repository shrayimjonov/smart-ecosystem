package uz.uzkassa.smartpossupply.web.rest.pub;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.post.PostDetailDTO;
import uz.uzkassa.smartpossupply.dto.post.PostListDTO;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.service.PostService;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 02.11.2023
 */
@RestController
@RequestMapping(ApiConstants.publicPostRootApi)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PublicPostResource {

    PostService postService;

    @GetMapping(ApiConstants.id)
    public ResponseEntity<PostDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(postService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<PostListDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(postService.getList(filter));
    }
}
