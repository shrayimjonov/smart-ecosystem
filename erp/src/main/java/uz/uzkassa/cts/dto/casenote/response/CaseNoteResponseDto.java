package uz.uzkassa.cts.dto.casenote.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CaseNoteResponseDto {
    private Long id;
    private Long creatorId;
    private String creatorFullName;
    private Long lastModifierId;
    private String lastModifierFullName;
    private LocalDateTime createdDate;
    private String message;
    private String messageType;
}
