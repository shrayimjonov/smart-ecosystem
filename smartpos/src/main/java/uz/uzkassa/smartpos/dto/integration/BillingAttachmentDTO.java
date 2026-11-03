package uz.uzkassa.smartpos.dto.integration;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.enums.EntityType;

import java.time.LocalDateTime;

@Getter
@Setter
public class BillingAttachmentDTO {
    private Long id;

    private String fileName;

    private String originalFileName;

    @NotBlank
    private String url;

    private Boolean temp;

    private LocalDateTime createdDate;

    private EntityType relatedTo;

    private String relatedToId;
}
