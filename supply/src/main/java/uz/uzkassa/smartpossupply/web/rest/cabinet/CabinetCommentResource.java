package uz.uzkassa.smartpossupply.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.application.CommentDTO;
import uz.uzkassa.smartpossupply.dto.application.CommentListDTO;
import uz.uzkassa.smartpossupply.enums.CommentType;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.service.CommentService;

import java.util.ArrayList;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Rayimjonov Shuxratjon
 * Date: 07.11.2023
 */
@RestController
@RequestMapping(ApiConstants.cabinetCommentRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetCommentResource {

    CommentService commentService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody CommentDTO commentDTO) {
        commentDTO.setFromSupplier(true);
        return ResponseEntity.ok(commentService.create(commentDTO));
    }

    @GetMapping
    public ResponseEntity<Page<CommentListDTO>> getComments(@ParameterObject BaseFilter filter) {
        if (filter.getId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        filter.setType(CommentType.CHAT.getCode());
        return ResponseEntity.ok(commentService.getComments(filter));
    }
}
