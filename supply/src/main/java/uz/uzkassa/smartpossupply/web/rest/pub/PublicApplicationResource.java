package uz.uzkassa.smartpossupply.web.rest.pub;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.application.PublicApplicationDTO;
import uz.uzkassa.smartpossupply.service.CommentService;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Rayimjonov Shuxratjon
 * Date: 07.11.2023
 */
@RestController
@RequestMapping(ApiConstants.publicApplicationRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PublicApplicationResource {

    CommentService commentService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody PublicApplicationDTO publicApplicationDTO) {
        return ResponseEntity.ok(commentService.create(publicApplicationDTO));
    }
}
