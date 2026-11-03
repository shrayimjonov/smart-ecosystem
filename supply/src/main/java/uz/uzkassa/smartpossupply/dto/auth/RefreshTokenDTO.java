package uz.uzkassa.smartpossupply.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record RefreshTokenDTO(@NotBlank @JsonProperty("refresh_token") String refreshToken) {
}
