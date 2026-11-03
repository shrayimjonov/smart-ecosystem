package uz.uzkassa.smartpossupply.dto.integration;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpossupply.enums.AuditEntityType;

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

    private AuditEntityType relatedTo;

    private String relatedToId;
}
