package uz.uzkassa.horeca.web.rest.errors;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
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
import uz.uzkassa.horeca.service.LocalizationService;
import uz.uzkassa.horeca.service.TelegramService;
import uz.uzkassa.horeca.utils.HeaderUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestControllerAdvice
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private final LocalizationService localizationService;
    private final TelegramService telegramService;
    private final ObjectMapper objectMapper;

    public GlobalExceptionHandler(
            LocalizationService localizationService,
            TelegramService telegramService,
            ObjectMapper objectMapper) {
        this.localizationService = localizationService;
        this.telegramService = telegramService;
        this.objectMapper = objectMapper;
    }

    @ExceptionHandler(AppGlobalException.class)
    public ResponseEntity<ErrorDTO> appGlobalExceptionHandler(AppGlobalException ex, HttpServletRequest request) {
        sendMessageToTelegramBot(ex.getMessage(), ex.getStatus(), request);
        return get(ex.getTitle(), ex.getMessage(), ex.getStatus(), request.getRequestURI());
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

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDTO> runtimeExceptionHandler(RuntimeException ex, HttpServletRequest request) {
        if (StringUtils.isEmpty(ex.getMessage())) {
            Throwable throwable = ex.getCause();
            if (throwable instanceof AppGlobalException) {
                return this.getMessage(throwable, request);
            }
        }
        sendMessageToTelegramBot(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
        return get(ex.getMessage(), ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURI());
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorDTO> nullPointerExceptionHandler(NullPointerException ex, HttpServletRequest request) {
        if (StringUtils.isEmpty(ex.getMessage())) {
            Throwable throwable = ex.getCause();
            if (throwable instanceof AppGlobalException) {
                return this.getMessage(throwable, request);
            }
        }
        StringBuilder detailMessage = new StringBuilder();
        detailMessage.append("NULL POINTER EXCEPTION: ");
        detailMessage.append(Optional.ofNullable(ex.getMessage()).orElse(""));
        detailMessage.append(
                Arrays
                        .stream(ex.getStackTrace())
                        .map(StackTraceElement::toString)
                        .filter(s -> s.contains("uz.uzkassa.smartpossupply"))
                        .map(s -> s.replaceAll("[<,>]", ""))
                        .collect(Collectors.joining(" \n "))
        );
        sendMessageToTelegramBot(detailMessage.toString(), HttpStatus.INTERNAL_SERVER_ERROR, request);
        return get(
                localizationService.localize("null.value.error"),
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
        return get(ex.getMessage(), ex.getMessage(), HttpStatus.UNAUTHORIZED, request.getRequestURI());
    }

    @ExceptionHandler(ApayIntegrationException.class)
    public ResponseEntity<ErrorDTO> apayIntegrationExceptionHandler(ApayIntegrationException ex, HttpServletRequest request) {
        try {
            log.info("===========Start convert message============: {}", ex.getMessage());
            String apayError = ex.getMessage();
            if (StringUtils.isNotEmpty(apayError)) {
                if (apayError.contains("{") && apayError.contains("}")) {
                    apayError = apayError.substring(apayError.indexOf("{"));
                    apayError = apayError.substring(0,apayError.indexOf("}")+1);
                }
            }
            apayError = apayError.replaceAll("<EOL>", "");
            Map<String,Object>body=objectMapper.readValue(apayError,Map.class);
            if (body!=null&&body.containsKey("code")) {
                String title = String.valueOf(body.get("title"));
                if (StringUtils.isNotEmpty(title)){
                    return get(title, String.valueOf(body.get("detail")), HttpStatus.BAD_REQUEST, request.getRequestURI());
                }
                throw new BadRequestException(localizationService.localize("bad.request"));
            }else{
                throw new BadRequestException(localizationService.localize("bad.request"));
            }
        } catch (Exception e) {
            log.info("Object read error: {}", e.getMessage());
        }
        sendMessageToTelegramBot(ex.getMessage(), HttpStatus.BAD_REQUEST, request);
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
        return get(ex.getTitle(), ex.getMessage(), HttpStatus.UNAUTHORIZED, request.getRequestURI());
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
                    localizationService.localize("method.argument.invalid.error"),
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
        return get(title, message, status, path, null);
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
        this.sendMessageToTelegramBot(message, status, request, null);
    }

    private void sendMessageToTelegramBot(String message, HttpStatus status, HttpServletRequest request, String entityId) {
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
        if (StringUtils.isNotEmpty(entityId)) {
            messageBuilder.append("<b>Entity Id:</b> ").append(entityId).append("\n");
        }
        if (StringUtils.isNotEmpty(request.getRequestURI())) {
            messageBuilder.append("<b>API:</b> ").append(request.getRequestURI()).append("\n");
        }
        if (StringUtils.isNotEmpty(HeaderUtils.getCurrentUserIP())) {
            messageBuilder.append("<b>IP Address:</b> ").append(HeaderUtils.getCurrentUserIP()).append("\n");
        }
        if (StringUtils.isNotEmpty(user)) {
            messageBuilder.append("<b>User:</b> ").append(user).append("\n");
        }
        if (StringUtils.isNotEmpty(HeaderUtils.getCurrentCompanyTIN())) {
            messageBuilder.append("<b>Company TIN:</b> ").append(HeaderUtils.getCurrentCompanyTIN()).append("\n");
        }
        messageBuilder.append("<b>Date:</b> ").append(LocalDateTime.now()).append("\n");
        messageBuilder.append("<b>Message:</b> ").append(message).append("\n");

        telegramService.send(messageBuilder.toString());
        log.error(messageBuilder.toString());
    }
}
