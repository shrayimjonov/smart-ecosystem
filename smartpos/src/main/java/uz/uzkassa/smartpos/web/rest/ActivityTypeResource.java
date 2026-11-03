package uz.uzkassa.smartpos.web.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.domain.postgres.company.ActivityType;
import uz.uzkassa.smartpos.dto.ActivityTypeDTO;
import uz.uzkassa.smartpos.exceptions.EntityNotFound;
import uz.uzkassa.smartpos.repository.postgres.common.ActivityTypeRepository;
import uz.uzkassa.smartpos.service.LocalizationService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for managing {@link ActivityType}.
 */
@RestController
@RequestMapping("/api")
public class ActivityTypeResource {

    private final ActivityTypeRepository activityTypeRepository;

    private final LocalizationService localizationService;

    public ActivityTypeResource(ActivityTypeRepository activityTypeRepository, LocalizationService localizationService) {
        this.activityTypeRepository = activityTypeRepository;
        this.localizationService = localizationService;
    }

    /**
     * {@code GET  /activity-types} : get all the activityTypes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of activityTypes in body.
     */
    @GetMapping("/activity-types")
    public List<ActivityTypeDTO> getAllActivityTypes(@RequestParam(required = false) Long parentId) {
        List<ActivityType> activityTypes = parentId != null ? activityTypeRepository.findAllByParentId(parentId)
                : activityTypeRepository.findAll();
        return activityTypes.stream().map(ActivityType::getDTO).collect(Collectors.toList());
    }

    /**
     * {@code GET  /activity-types/:id} : get the "id" activityType.
     *
     * @param id the id of the activityType to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the activityType, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/activity-types/{id}")
    public ResponseEntity<ActivityTypeDTO> getActivityType(@PathVariable Long id) {
        ActivityType activityType = activityTypeRepository.findById(id).orElseThrow(() -> new EntityNotFound(localizationService.getMessage("activity.type", id)));
        return ResponseEntity.ok(activityType.getDTO());
    }
}
