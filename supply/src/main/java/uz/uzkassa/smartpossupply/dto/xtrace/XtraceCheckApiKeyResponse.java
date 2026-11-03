package uz.uzkassa.smartpossupply.dto.xtrace;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record XtraceCheckApiKeyResponse(Boolean isTinCorrect, LocalDateTime expiresOn) implements Serializable {
}
