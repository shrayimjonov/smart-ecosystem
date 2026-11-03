package uz.uzkassa.smartpossupply.web.rest.admin;

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
import uz.uzkassa.smartpossupply.dto.application.ApplicationDetailDTO;
import uz.uzkassa.smartpossupply.dto.application.ApplicationListDTO;
import uz.uzkassa.smartpossupply.enums.CommentType;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.service.CommentService;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Rayimjonov Shuxratjon
 * Date: 07.11.2023
 */
@RestController
@RequestMapping(ApiConstants.adminApplicationRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminApplicationResource {

    CommentService commentService;

    @GetMapping(ApiConstants.id)
    public ResponseEntity<ApplicationDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<ApplicationListDTO>> getList(@ParameterObject BaseFilter filter) {
        filter.setType(CommentType.APPLICATION.getCode());
        return ResponseEntity.ok(commentService.getList(filter));
    }
}
