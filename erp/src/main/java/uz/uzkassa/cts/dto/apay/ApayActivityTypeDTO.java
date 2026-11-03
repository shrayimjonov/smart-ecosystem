package uz.uzkassa.cts.dto.apay;


import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.domain.ActivityType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApayActivityTypeDTO extends ApayLocalizedDTO {

    ApayLocalizedDTO parent;

    public ApayActivityTypeDTO(ActivityType activityType) {
        this.setId(activityType.getId().toString());
        this.setCode(activityType.getCode());
        this.setNameUz(activityType.getNameUzLatin());
        this.setNameRu(activityType.getNameRu());
        if (activityType.getParent() != null) {
            ApayActivityTypeDTO parent = new ApayActivityTypeDTO();
            parent.setId(activityType.getParent().getId().toString());
            parent.setCode(activityType.getParent().getCode());
            parent.setNameUz(activityType.getParent().getNameUzLatin());
            parent.setNameRu(activityType.getParent().getNameRu());
            this.setParent(parent);
        }
    }
}
