package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.domain.Thymeleaf;
import uz.uzkassa.cts.dto.template.ThymeleafDTO;
import uz.uzkassa.cts.service.ThymeleafService;

/**
 * REST controller for managing {@link Thymeleaf}.
 */
@RestController
@RequestMapping("/api/templates")
@Tag(name = "Template")
@Slf4j
public class ThymeleafResource {

    private final ThymeleafService thymeleafService;

    public ThymeleafResource(ThymeleafService thymeleafService) {
        this.thymeleafService = thymeleafService;
    }

    @Operation(summary ="Generate Template")
    @PostMapping("/generate")
    public ResponseEntity<String> generate(@RequestBody ThymeleafDTO thymeleafDTO) {
        log.debug("REST request to generate new TemplateDTO: {}", thymeleafDTO);
        String generateHTML = thymeleafService.generateHTML(thymeleafDTO);
        return ResponseEntity.ok(generateHTML);
    }
}
