package uz.uzkassa.smartpos.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpos.exceptions.GlobalExceptionResponse;
import uz.uzkassa.smartpos.service.LocalizationService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@AllArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;
    private final LocalizationService localizationService;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        GlobalExceptionResponse problem = GlobalExceptionResponse.builder()
                .title(localizationService.getMessage("unauthorized.error"))
                .detail(localizationService.getMessage("unauthorized.error"))
                .message(localizationService.getMessage("unauthorized.error"))
                .solution(localizationService.getMessage("unauthorized.error.solution"))
                .build();
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        objectMapper.writeValue(response.getWriter(), problem);
    }
}
