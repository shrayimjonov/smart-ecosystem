package uz.uzkassa.smartpossupply.dto.xtrace;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record XtraceRefreshApiKeyResponse(String apiKey, String id, LocalDateTime expiresOn) implements Serializable {
}
