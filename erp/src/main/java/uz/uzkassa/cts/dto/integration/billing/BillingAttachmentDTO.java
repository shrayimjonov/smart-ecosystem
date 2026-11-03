package uz.uzkassa.cts.dto.integration.billing;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.billing.EntityType;

import java.time.LocalDateTime;

@Getter
@Setter
public class BillingAttachmentDTO {
    private Long id;

    private String fileName;

    private String originalFileName;

    @NotNull
    private String url;

    private Boolean temp;

    private LocalDateTime createdDate;

    private EntityType relatedTo;

    private String relatedToId;
}
