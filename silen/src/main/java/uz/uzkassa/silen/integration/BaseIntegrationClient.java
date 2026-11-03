package uz.uzkassa.silen.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import uz.uzkassa.silen.config.httpclient.CustomRequest;
import uz.uzkassa.silen.config.httpclient.CustomResponse;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.exceptions.EntityNotFoundException;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public abstract class BaseIntegrationClient {
    CustomRequest customRequest;
    ObjectMapper objectMapper;
    String startEndpoint;

    public BaseIntegrationClient(CustomRequest customRequest, ObjectMapper objectMapper, String startEndpoint) {
        this.customRequest = customRequest;
        this.objectMapper = objectMapper;
        this.startEndpoint = startEndpoint;
    }

    protected abstract Map<String, String> evaluateHeaders(String token);

    protected abstract String getBaseUrl();


    public <T> T convertToResponse(final CustomResponse customResponse, final Class<T> toDto) {
        final String errorMessage = StringUtils.isNotEmpty(customResponse.getErrorMessage()) ? customResponse.getErrorMessage() : customResponse.getContent();
        switch (HttpStatus.valueOf(customResponse.getCode())) {
            case HttpStatus.OK -> {
                try {
                    return objectMapper.readValue(customResponse.getContent(), toDto);
                } catch (final IOException e) {
                    log.error("BaseIntegrationClient response convert to JSON: {}", e.getMessage());
                }
            }
            case HttpStatus.CREATED -> {
                if (customResponse.getContent() == null) return null;
                return objectMapper.convertValue(customResponse.getContent(), toDto);
            }
            case NOT_FOUND -> throw new EntityNotFoundException(errorMessage);
            case BAD_REQUEST, INTERNAL_SERVER_ERROR -> throw new BadRequestException(errorMessage);
            default -> {
                log.error("Response convert error: {}", customResponse);
                throw new BadRequestException(errorMessage);
            }
        }
        return null;
    }

    protected String convertBodyToJsonString(Object body) {
        try {
            return objectMapper.writeValueAsString(body);
        } catch (final JsonProcessingException e) {
            log.error("Error convert body to JSON : {}", e.getMessage(), e);
            throw new BadRequestException(e.getMessage());
        }
    }
}
