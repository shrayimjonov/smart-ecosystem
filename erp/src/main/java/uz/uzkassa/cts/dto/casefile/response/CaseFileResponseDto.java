package uz.uzkassa.cts.dto.casefile.response;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.DocumentType;

import java.time.LocalDateTime;

@Getter
@Setter
public class CaseFileResponseDto {
    private Long id;
    private Long creatorId;
    private String creatorFullName;
    private Long lastModifierId;
    private String lastModifierFullName;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private String fileUrl;
    private DocumentType documentType;
    private String description;
}
