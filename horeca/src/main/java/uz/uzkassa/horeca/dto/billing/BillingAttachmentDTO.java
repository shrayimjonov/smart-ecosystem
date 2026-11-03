package uz.uzkassa.horeca.dto.billing;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.horeca.enums.billing.AuditEntityType;

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
