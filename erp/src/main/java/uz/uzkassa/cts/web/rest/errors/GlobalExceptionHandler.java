package uz.uzkassa.cts.web.rest.errors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.social.TelegramService;
import uz.uzkassa.cts.validator.LocalizationMessage;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestControllerAdvice
@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final LocalizationMessage localizationMessage;
    private final TelegramService telegramService;
    private final ObjectMapper objectMapper;

    @ExceptionHandler(AppGlobalException.class)
    public ResponseEntity<ErrorDTO> appGlobalExceptionHandler(AppGlobalException ex, HttpServletRequest request) {
        sendMessageToTelegramBot(ex.getMessage(), ex.getStatus(), request);
        return get(ex.getTitle(), ex.getMessage(), ex.getStatus(), request.getRequestURI());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorDTO> badRequestExceptionHandler(BadRequestException ex, HttpServletRequest request) {
        sendMessageToTelegramBot(ex.getMessage(), ex.getStatus(), request);
        return get(ex.getTitle(), ex.getMessage(), ex.getStatus(), request.getRequestURI());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorDTO> conflictExceptionHandler(ConflictException ex, HttpServletRequest request) {
        sendMessageToTelegramBot(ex.getMessage(), ex.getStatus(), request);
        return get(ex.getTitle(), ex.getMessage(), ex.getStatus(), request.getRequestURI());
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorDTO> forbiddenExceptionHandler(ForbiddenException ex, HttpServletRequest request) {
        sendMessageToTelegramBot(ex.getMessage(), ex.getStatus(), request);
        return get(ex.getTitle(), ex.getMessage(), ex.getStatus(), request.getRequestURI());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorDTO> notFoundExceptionHandler(NotFoundException ex, HttpServletRequest request) {
        sendMessageToTelegramBot(ex.getMessage(), ex.getStatus(), request);
        return get(ex.getTitle(), ex.getMessage(), ex.getStatus(), request.getRequestURI());
    }

    @ExceptionHandler(UserNotActivatedException.class)
    public ResponseEntity<ErrorDTO> userNotActivatedHandler(UserNotActivatedException ex, HttpServletRequest request) {
        sendMessageToTelegramBot(ex.getMessage(), HttpStatus.FORBIDDEN, request);
        return get(ex.getMessage(), ex.getMessage(), HttpStatus.FORBIDDEN, request.getRequestURI());
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ErrorDTO> resourceAccessExceptionHandler(ResourceAccessException ex, HttpServletRequest request) {
        sendMessageToTelegramBot(ex.getMessage(), HttpStatus.FORBIDDEN, request);
        return get(ex.getMessage(), ex.getMessage(), HttpStatus.FORBIDDEN, request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> genericExceptionHandler(Exception ex, HttpServletRequest request) {
        if (StringUtils.isEmpty(ex.getMessage())) {
            Throwable throwable = ex.getCause();
            if (throwable instanceof AppGlobalException) {
                return this.getMessage(throwable, request);
            }
        }
        sendMessageToTelegramBot(ex.getMessage(), HttpStatus.BAD_REQUEST, request);
        return get(ex.getMessage(), ex.getMessage(), HttpStatus.BAD_REQUEST, request.getRequestURI());
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorDTO> httpClientErrorExceptionHandler(HttpClientErrorException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        sendMessageToTelegramBot(ex.getMessage(), status, request);
        Map<String, Object> response;
        try {
            response = objectMapper.readValue(ex.getResponseBodyAsString(StandardCharsets.UTF_8), new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return get(String.valueOf(response.getOrDefault("title", "Произошла ошибка при запросе")), String.valueOf(response.getOrDefault("detail", "Произошла ошибка при запросе")), status, request.getRequestURI());
    }

    @ExceptionHandler(IntegrationException.class)
    public ResponseEntity<ErrorDTO> integrationExceptionHandler(IntegrationException ex, HttpServletRequest request) {
        if (ex.getError() != null) {
            sendMessageToTelegramBot(ex.getError().getMessage(), ex.getStatus(), request);
            return get(ex.getError().getTitle(), ex.getError().getMessage(), ex.getStatus(), request.getRequestURI());
        } else {
            sendMessageToTelegramBot(ex.getMessage(), ex.getStatus(), request);
            return get(ex.getMessage(), ex.getMessage(), Optional.ofNullable(ex.getStatus()).orElse(HttpStatus.BAD_REQUEST), request.getRequestURI());
        }
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDTO> runtimeExceptionHandler(RuntimeException ex, HttpServletRequest request) {
        if (StringUtils.isEmpty(ex.getMessage())) {
            Throwable throwable = ex.getCause();
            if (throwable instanceof AppGlobalException) {
                return this.getMessage(throwable, request);
            }
        }
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if (ex instanceof NotFoundException)
            status = HttpStatus.NOT_FOUND;
        sendMessageToTelegramBot(ex.getMessage(), status, request);
        return get(ex.getMessage(), ex.getMessage(), status, request.getRequestURI());
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorDTO> nullPointerExceptionHandler(NullPointerException ex, HttpServletRequest request) {
        if (StringUtils.isEmpty(ex.getMessage())) {
            Throwable throwable = ex.getCause();
            if (throwable instanceof AppGlobalException) {
                return this.getMessage(throwable, request);
            }
        }
        String detailMessage = "NULL POINTER EXCEPTION: " +
                Optional.ofNullable(ex.getMessage()).orElse("") +
                Arrays
                        .stream(ex.getStackTrace())
                        .map(StackTraceElement::toString)
                        .filter(s -> s.contains("uz.uzkassa.smartpossupply"))
                        .map(s -> s.replaceAll("[<,>]", ""))
                        .collect(Collectors.joining(" \n "));
        sendMessageToTelegramBot(detailMessage, HttpStatus.INTERNAL_SERVER_ERROR, request);
        return get(
                localizationMessage.localize("null.value.error"),
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                request.getRequestURI()
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorDTO> authenticationExceptionHandler(AuthenticationException ex, HttpServletRequest request) {
        if (StringUtils.isEmpty(ex.getMessage())) {
            Throwable throwable = ex.getCause();
            if (throwable instanceof AppGlobalException) {
                return this.getMessage(throwable, request);
            }
        }
        sendMessageToTelegramBot(ex.getMessage(), HttpStatus.UNAUTHORIZED, request);
        return get(ex.getMessage(), ex.getMessage(), HttpStatus.BAD_REQUEST, request.getRequestURI());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorDTO> unauthorizedExceptionHandler(UnauthorizedException ex, HttpServletRequest request) {
        if (StringUtils.isEmpty(ex.getMessage())) {
            Throwable throwable = ex.getCause();
            if (throwable instanceof AppGlobalException) {
                return this.getMessage(throwable, request);
            }
        }
        sendMessageToTelegramBot(ex.getMessage(), HttpStatus.UNAUTHORIZED, request);
        return get(ex.getTitle(), ex.getMessage(), HttpStatus.BAD_REQUEST, request.getRequestURI());
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorDTO> validationExceptionHandler(BindException ex, HttpServletRequest request) {
        if (StringUtils.isEmpty(ex.getMessage())) {
            Throwable throwable = ex.getCause();
            if (throwable instanceof AppGlobalException) {
                return this.getMessage(throwable, request);
            }
        }
        if (ex instanceof MethodArgumentNotValidException) {
            BindingResult result = ex.getBindingResult();
            List<FieldErrorVM> fieldErrors = result
                    .getFieldErrors()
                    .stream()
                    .map(f ->
                            new FieldErrorVM(
                                    f.getObjectName().replaceFirst("DTO$", ""),
                                    f.getField(),
                                    StringUtils.isNotBlank(f.getDefaultMessage()) ? f.getDefaultMessage() : f.getCode()
                            )
                    )
                    .collect(Collectors.toList());

            sendMessageToTelegramBot(ex.getMessage(), HttpStatus.BAD_REQUEST, request);
            return get(
                    localizationMessage.localize("method.argument.invalid.error"),
                    ex.getMessage(),
                    HttpStatus.BAD_REQUEST,
                    request.getRequestURI(),
                    fieldErrors
            );
        }
        sendMessageToTelegramBot(ex.getMessage(), HttpStatus.BAD_REQUEST, request);
        return get(ex.getMessage(), ex.getMessage(), HttpStatus.BAD_REQUEST, request.getRequestURI());
    }

    private ResponseEntity<ErrorDTO> getMessage(Throwable throwable, HttpServletRequest request) {
        AppGlobalException ex = (AppGlobalException) throwable;
        sendMessageToTelegramBot(ex.getMessage(), ex.status, request);
        return get(ex.getTitle(), ex.getMessage(), ex.status, request.getRequestURI());
    }

    private static ResponseEntity<ErrorDTO> get(String title, String message, HttpStatus status, String path) {
        return get(title, message, status, path, new ArrayList<>());
    }

    private static ResponseEntity<ErrorDTO> get(
            String title,
            String message,
            HttpStatus status,
            String path,
            List<FieldErrorVM> errorFields
    ) {
        return new ResponseEntity<>(
                ErrorDTO
                        .builder()
                        .title(title)
                        .detail(message)
                        .status(status.value())
                        .path(path)
                        .timestamp(LocalDateTime.now().toString())
                        .errorFields(errorFields)
                        .build(),
                status
        );
    }

    private void sendMessageToTelegramBot(String message, HttpStatus status, HttpServletRequest request) {
        String user = null;
        if (request.toString().contains("user=")) {
            user = request.toString().split("user=")[1];
        } else if (request.getRemoteUser() != null) user = request.getRemoteUser();

        StringBuilder messageBuilder = new StringBuilder();
        if (status != null) {
            messageBuilder.append("<b>Type:</b> ").append(status.name()).append("\n");
            messageBuilder.append("<b>Status:</b> ").append(status.value()).append("\n");
        }
        if (StringUtils.isNotEmpty(request.getMethod())) {
            messageBuilder.append("<b>Method:</b> ").append(request.getMethod()).append("\n");
        }
        StringUtils.isNotEmpty(null);
        if (StringUtils.isNotEmpty(request.getRequestURI())) {
            messageBuilder.append("<b>API:</b> ").append(request.getRequestURI()).append("\n");
        }
        if (StringUtils.isNotEmpty(SecurityUtils.getCurrentUserIP())) {
            messageBuilder.append("<b>IP Address:</b> ").append(SecurityUtils.getCurrentUserIP()).append("\n");
        }
        if (StringUtils.isNotEmpty(user)) {
            messageBuilder.append("<b>User:</b> ").append(user).append("\n");
        }
        messageBuilder.append("<b>Date:</b> ").append(LocalDateTime.now()).append("\n");
        messageBuilder.append("<b>Message:</b> ").append(message).append("\n");

        telegramService.sendMessageToChannel(messageBuilder.toString());
        log.error(messageBuilder.toString());
    }
}
