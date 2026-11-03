package uz.uzkassa.smartpossupply.web.rest.errors;

import uz.uzkassa.smartpossupply.constants.MessageConstants;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.http.NoHttpResponseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import uz.uzkassa.smartpossupply.integration.billing.BillingExceptionDTO;
import uz.uzkassa.smartpossupply.integration.billing.BillingIntegrationException;
import uz.uzkassa.smartpossupply.service.LocalizationService;
import uz.uzkassa.smartpossupply.service.TelegramBotApplicationLogService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.net.SocketTimeoutException;
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

    private final ObjectMapper objectMapper;

    private final LocalizationService localizationService;

    private final TelegramBotApplicationLogService telegramApplicationLogService;

    public GlobalExceptionHandler(ObjectMapper objectMapper, LocalizationService localizationService, TelegramBotApplicationLogService telegramApplicationLogService) {
        this.objectMapper = objectMapper;
        this.localizationService = localizationService;
        this.telegramApplicationLogService = telegramApplicationLogService;
    }


    @ExceptionHandler(AppGlobalException.class)
    public ResponseEntity<ErrorDTO> appGlobalExceptionHandler(AppGlobalException ex, HttpServletRequest request) {
        sendMessageToTelegramBot(ex.getTitle(), ex.getMessage(), ex.getStatus(), request);
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setTitle(ex.getTitle());
        errorDTO.setDetail(ex.getMessage());
        errorDTO.setStatus(ex.getStatus().value());
        errorDTO.setPath(request.getRequestURI());
        errorDTO.setTimestamp(LocalDateTime.now().toString());
        return new ResponseEntity<>(
                errorDTO,
                ex.getStatus());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorDTO> badRequestExceptionHandler(BadRequestException ex, HttpServletRequest request) {
        sendMessageToTelegramBot(ex.getTitle(), ex.getMessage(), ex.getStatus(), request);
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setTitle(ex.getTitle());
        errorDTO.setDetail(ex.getMessage());
        errorDTO.setStatus(ex.getStatus().value());
        errorDTO.setPath(request.getRequestURI());
        errorDTO.setTimestamp(LocalDateTime.now().toString());
        return new ResponseEntity<>(
                errorDTO,
                ex.getStatus());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorDTO> conflictExceptionHandler(ConflictException ex, HttpServletRequest request) {
        sendMessageToTelegramBot(ex.getTitle(), ex.getMessage(), ex.getStatus(), request);
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setTitle(ex.getTitle());
        errorDTO.setDetail(ex.getMessage());
        errorDTO.setStatus(ex.getStatus().value());
        errorDTO.setPath(request.getRequestURI());
        errorDTO.setTimestamp(LocalDateTime.now().toString());
        return new ResponseEntity<>(
                errorDTO,
                ex.getStatus());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorDTO> notFoundExceptionHandler(NotFoundException ex, HttpServletRequest request) {
        sendMessageToTelegramBot(ex.getTitle(), ex.getMessage(), ex.getStatus(), request);
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setTitle(ex.getTitle());
        errorDTO.setDetail(ex.getMessage());
        errorDTO.setStatus(ex.getStatus().value());
        errorDTO.setPath(request.getRequestURI());
        errorDTO.setTimestamp(LocalDateTime.now().toString());
        return new ResponseEntity<>(
                errorDTO,
                ex.getStatus());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorDTO> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        sendMessageToTelegramBot(ex.getTitleMessageCode(), ex.getMessage(), HttpStatus.BAD_REQUEST, request);
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setTitle(localizationService.localize(MessageConstants.ERROR));
        errorDTO.setDetail(localizationService.localize(MessageConstants.METHOD_NOT_SUPPORTED));
        errorDTO.setStatus(HttpStatus.BAD_REQUEST.value());
        errorDTO.setPath(request.getRequestURI());
        errorDTO.setTimestamp(LocalDateTime.now().toString());
        return ResponseEntity.badRequest().body(errorDTO);
    }

    @ExceptionHandler(SmartposIntegrationException.class)
    public ResponseEntity<ErrorDTO> smartposIntegrationExceptionHandler(SmartposIntegrationException ex, HttpServletRequest request) {
        try {
            String apayError = ex.getMessage();
            if (StringUtils.isNotEmpty(apayError)) {
                if (apayError.contains("{") && apayError.contains("}")) {
                    apayError = apayError.substring(apayError.indexOf("{"));
                    apayError = apayError.substring(0, apayError.indexOf("}") + 1);
                }
            }
            apayError = apayError.replaceAll("<EOL>", "");
            Map<String, Object> body = objectMapper.readValue(apayError, Map.class);
            if (body != null && body.containsKey("code")) {
                String title = String.valueOf(body.get("title"));
                if (StringUtils.isNotEmpty(title)) {
                    ErrorDTO errorDTO = new ErrorDTO();
                    errorDTO.setTitle(title);
                    errorDTO.setDetail(String.valueOf(body.get("detail")));
                    errorDTO.setStatus(HttpStatus.BAD_REQUEST.value());
                    errorDTO.setPath(request.getRequestURI());
                    errorDTO.setTimestamp(LocalDateTime.now().toString());
                    return new ResponseEntity<>(
                            errorDTO,
                            ex.getStatus());
                }
                throw new BadRequestException(localizationService.localize("bad.request"));
            } else {
                throw new BadRequestException(localizationService.localize("bad.request"));
            }
        } catch (Exception e) {
            log.info("Object read error: {}", e.getMessage());
        }
        sendMessageToTelegramBot(ex.getTitle(), ex.getMessage(), HttpStatus.BAD_REQUEST, request);
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setTitle(ex.getTitle());
        errorDTO.setDetail(ex.getMessage());
        errorDTO.setStatus(HttpStatus.BAD_REQUEST.value());
        errorDTO.setPath(request.getRequestURI());
        errorDTO.setTimestamp(LocalDateTime.now().toString());
        return ResponseEntity.badRequest().body(errorDTO);
    }

    @ExceptionHandler(IntegrationException.class)
    public ResponseEntity<ErrorDTO> integrationExceptionHandler(IntegrationException ex, HttpServletRequest request) {
        IntegrationExceptionDTO integrationExceptionDTO = null;
        int statusCode = StringUtils.isNumeric(ex.getMessage().substring(0, 3)) ? Integer.parseInt(ex.getMessage().substring(0, 3)) : HttpStatus.BAD_REQUEST.value();
        String messageDetail = null;
        try {
            String message = ex.getMessage();

            if (message.contains("[")) {
                message = message.split("\\[")[1];
            }
            if (message.contains("]")) {
                message = message.split("\\]")[0];
            }
            if (!message.contains("<html>")) {
                integrationExceptionDTO = objectMapper.readValue(message, IntegrationExceptionDTO.class);
            } else {
                messageDetail = message;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        if (integrationExceptionDTO != null) {
            HttpStatus status = ex.getStatus() != null ? ex.getStatus() : HttpStatus.valueOf(statusCode);
            if (HttpStatus.UNAUTHORIZED.equals(status)) {
                status = HttpStatus.BAD_REQUEST;
            }
            sendMessageToTelegramBot(ex.getTitle(), ex.getMessage(), status, request);

            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setTitle(ex.getTitle());
            errorDTO.setDetail(integrationExceptionDTO.getDetail());
            errorDTO.setStatus(status.value());
            errorDTO.setPath(integrationExceptionDTO.getPath());
            errorDTO.setTimestamp(LocalDateTime.now().toString());
            return new ResponseEntity<>(
                    errorDTO,
                    status);
        } else {
            if (StringUtils.isEmpty(ex.getMessage())) {
                Throwable throwable = ex.getCause();
                if (throwable instanceof AppGlobalException) {
                    return this.getMessage(throwable, request);
                }
            }
            HttpStatus status = ex.getStatus() != null ? ex.getStatus() : HttpStatus.valueOf(statusCode);
            if (HttpStatus.UNAUTHORIZED.equals(status)) {
                status = HttpStatus.BAD_REQUEST;
            }
            sendMessageToTelegramBot(ex.getTitle(), ex.getMessage(), status, request);
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setTitle(ex.getTitle());
            errorDTO.setDetail(Optional.ofNullable(messageDetail).orElse(ex.getMessage()));
            errorDTO.setStatus(status.value());
            errorDTO.setPath(request.getRequestURI());
            errorDTO.setTimestamp(LocalDateTime.now().toString());
            return new ResponseEntity<>(
                    errorDTO,
                    status);
        }
    }

    @ExceptionHandler(FacturaException.class)
    public ResponseEntity<ErrorDTO> facturaExceptionHandler(FacturaException ex, HttpServletRequest request) {
        HttpStatus status;
        if (ex.getStatus() != null) {
            status = ex.getStatus();
        } else {
            status = StringUtils.isNumeric(ex.getMessage().substring(0, 3)) ? HttpStatus.valueOf(Integer.parseInt(ex.getMessage().substring(0, 3))) : HttpStatus.BAD_REQUEST;
        }
        if (StringUtils.isEmpty(ex.getMessage())) {
            Throwable throwable = ex.getCause();
            if (throwable instanceof AppGlobalException) {
                return this.getMessage(throwable, request);
            }
        }

        if (HttpStatus.UNAUTHORIZED.equals(status)) {
            status = HttpStatus.BAD_REQUEST;
        }

        sendMessageToTelegramBot(ex.getTitle(), ex.getMessage(), status, request, ex.getEntityId());
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setTitle(ex.getTitle());
        errorDTO.setDetail(ex.getMessage());
        errorDTO.setStatus(status.value());
        errorDTO.setCustomStatus(ex.getCustomStatus());
        errorDTO.setPath(request.getRequestURI());
        errorDTO.setTimestamp(LocalDateTime.now().toString());
        return new ResponseEntity<>(
                errorDTO,
                status);
    }

    @ExceptionHandler(XtraceException.class)
    public ResponseEntity<ErrorDTO> xtraceExceptionHandler(XtraceException ex, HttpServletRequest request) {
        HttpStatus status = ex.getStatus() != null ? ex.getStatus() : StringUtils.isNumeric(ex.getMessage().substring(0, 3)) ? HttpStatus.valueOf(Integer.parseInt(ex.getMessage().substring(0, 3))) : HttpStatus.BAD_REQUEST;
        if (StringUtils.isEmpty(ex.getMessage())) {
            Throwable throwable = ex.getCause();
            if (throwable instanceof AppGlobalException) {
                return this.getMessage(throwable, request);
            }
        }

        if (HttpStatus.UNAUTHORIZED.equals(status)) {
            status = HttpStatus.BAD_REQUEST;
        }

        sendMessageToTelegramBot(ex.getTitle(), ex.getMessage(), status, request, ex.getEntityId());

        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setTitle(ex.getTitle());
        errorDTO.setDetail(ex.getMessage());
        errorDTO.setStatus(status.value());
        errorDTO.setCustomStatus(ex.getCustomStatus());
        errorDTO.setPath(request.getRequestURI());
        errorDTO.setTimestamp(LocalDateTime.now().toString());
        return new ResponseEntity<>(errorDTO, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> genericExceptionHandler(Exception ex, HttpServletRequest request) {
        if (StringUtils.isEmpty(ex.getMessage())) {
            Throwable throwable = ex.getCause();
            if (throwable instanceof AppGlobalException) {
                return this.getMessage(throwable, request);
            }
        }
        sendMessageToTelegramBot("Exception", ex.getMessage(), HttpStatus.BAD_REQUEST, request);
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setTitle(ex.getMessage());
        errorDTO.setDetail(ex.getMessage());
        errorDTO.setStatus(HttpStatus.BAD_REQUEST.value());
        errorDTO.setPath(request.getRequestURI());
        errorDTO.setTimestamp(LocalDateTime.now().toString());
        return ResponseEntity.badRequest().body(errorDTO);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDTO> runtimeExceptionHandler(RuntimeException ex, HttpServletRequest request) {
        if (StringUtils.isEmpty(ex.getMessage())) {
            Throwable throwable = ex.getCause();
            if (throwable instanceof AppGlobalException) {
                return this.getMessage(throwable, request);
            }
        }
        sendMessageToTelegramBot("RuntimeException", ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setTitle(ex.getMessage());
        errorDTO.setDetail(ex.getMessage());
        errorDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorDTO.setPath(request.getRequestURI());
        errorDTO.setTimestamp(LocalDateTime.now().toString());
        return new ResponseEntity<>(
                errorDTO,
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BillingIntegrationException.class)
    public ResponseEntity<ErrorDTO> handleRestClient(BillingIntegrationException ex, HttpServletRequest request) {
        BillingExceptionDTO dto = ex.getDto();
        int billingStatus = Integer.parseInt(dto.getStatus());
        HttpStatus status = switch (billingStatus) {
            case 400 -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.BAD_GATEWAY;
        };
        sendMessageToTelegramBot("BillingIntegrationException", ex.getMessage(), status, request);
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setTitle(localizationService.localize(dto.getTitle(), null, SecurityUtils.getLocale()));
        errorDTO.setDetail(dto.getDetail());
        errorDTO.setStatus(Integer.parseInt(dto.getStatus()));
        errorDTO.setPath(dto.getPath());
        errorDTO.setTimestamp(LocalDateTime.now().toString());
        return new ResponseEntity<>(
                errorDTO,
                status);
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
        detailMessage.append(Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).filter(s -> s.contains("uz.uzkassa.smartpossupply"))
                .map(s -> s.replaceAll("[<,>]", "")).collect(Collectors.joining(" \n ")));
        sendMessageToTelegramBot("NullPointerException", detailMessage.toString(), HttpStatus.INTERNAL_SERVER_ERROR, request);
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setTitle(localizationService.localize(MessageConstants.NULL_VALUE_ERROR));
        errorDTO.setDetail(localizationService.localize(MessageConstants.NULL_VALUE_ERROR_DETAIL));
        errorDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorDTO.setPath(request.getRequestURI());
        errorDTO.setTimestamp(LocalDateTime.now().toString());
        return new ResponseEntity<>(
                errorDTO,
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorDTO> authenticationExceptionHandler(AuthenticationException ex, HttpServletRequest request) {
        if (StringUtils.isEmpty(ex.getMessage())) {
            Throwable throwable = ex.getCause();
            if (throwable instanceof AppGlobalException) {
                return this.getMessage(throwable, request);
            }
        }
        sendMessageToTelegramBot("AuthenticationException", ex.getMessage(), HttpStatus.UNAUTHORIZED, request);
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setTitle(localizationService.localize(MessageConstants.UNAUTHORIZED_ERROR));
        errorDTO.setDetail(localizationService.localize(MessageConstants.UNAUTHORIZED_ERROR_DETAIL));
        errorDTO.setRefreshToken(true);
        errorDTO.setStatus(HttpStatus.UNAUTHORIZED.value());
        errorDTO.setPath(request.getRequestURI());
        errorDTO.setTimestamp(LocalDateTime.now().toString());
        return new ResponseEntity<>(
                errorDTO,
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorDTO> badCredentialExceptionHandler(BadCredentialsException ex, HttpServletRequest request) {
        if (StringUtils.isEmpty(ex.getMessage())) {
            Throwable throwable = ex.getCause();
            if (throwable instanceof AppGlobalException) {
                return this.getMessage(throwable, request);
            }
        }
        sendMessageToTelegramBot("BadCredentialsException", ex.getMessage(), HttpStatus.UNAUTHORIZED, request);
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setTitle(localizationService.localize(MessageConstants.UNAUTHORIZED_ERROR));
        errorDTO.setDetail(localizationService.localize(MessageConstants.UNAUTHORIZED_ERROR_DETAIL));
        errorDTO.setRefreshToken(true);
        errorDTO.setStatus(HttpStatus.UNAUTHORIZED.value());
        errorDTO.setPath(request.getRequestURI());
        errorDTO.setTimestamp(LocalDateTime.now().toString());
        return new ResponseEntity<>(
                errorDTO,
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(SocketTimeoutException.class)
    public ResponseEntity<ErrorDTO> socketTimeOutException(SocketTimeoutException ex, HttpServletRequest request) {
        if (StringUtils.isEmpty(ex.getMessage())) {
            Throwable throwable = ex.getCause();
            if (throwable instanceof AppGlobalException) {
                return this.getMessage(throwable, request);
            }
        }
        sendMessageToTelegramBot("SocketTimeoutException", ex.getMessage(), HttpStatus.REQUEST_TIMEOUT, request);
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setTitle(localizationService.localize(MessageConstants.ERROR));
        errorDTO.setDetail(localizationService.localize(MessageConstants.TIME_OUT_ERROR));
        errorDTO.setStatus(HttpStatus.REQUEST_TIMEOUT.value());
        errorDTO.setRefreshToken(true);
        errorDTO.setPath(request.getRequestURI());
        errorDTO.setTimestamp(LocalDateTime.now().toString());
        return new ResponseEntity<>(
                errorDTO,
                HttpStatus.REQUEST_TIMEOUT);
    }

    @ExceptionHandler(HttpServerErrorException.BadGateway.class)
    public ResponseEntity<ErrorDTO> timeOutException(HttpServerErrorException.BadGateway ex, HttpServletRequest request) {
        if (StringUtils.isEmpty(ex.getMessage())) {
            Throwable throwable = ex.getCause();
            if (throwable instanceof AppGlobalException) {
                return this.getMessage(throwable, request);
            }
        }
        sendMessageToTelegramBot("HttpServerErrorException.BadGateway", ex.getMessage(), HttpStatus.BAD_GATEWAY, request);
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setTitle(localizationService.localize(MessageConstants.ERROR));
        errorDTO.setDetail(localizationService.localize(MessageConstants.TIME_OUT_ERROR));
        errorDTO.setRefreshToken(true);
        errorDTO.setStatus(HttpStatus.BAD_GATEWAY.value());
        errorDTO.setPath(request.getRequestURI());
        errorDTO.setTimestamp(LocalDateTime.now().toString());
        return new ResponseEntity<>(
                errorDTO,
                HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ErrorDTO> resourceAccessException(ResourceAccessException ex, HttpServletRequest request) {
        if (StringUtils.isEmpty(ex.getMessage())) {
            Throwable throwable = ex.getCause();
            if (throwable instanceof AppGlobalException) {
                return this.getMessage(throwable, request);
            }
        }
        sendMessageToTelegramBot("ResourceAccessException", ex.getMessage(), HttpStatus.BAD_GATEWAY, request);
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setTitle(localizationService.localize(MessageConstants.ERROR));
        errorDTO.setDetail(ex.getMessage());
        errorDTO.setRefreshToken(true);
        errorDTO.setStatus(HttpStatus.BAD_GATEWAY.value());
        errorDTO.setPath(request.getRequestURI());
        errorDTO.setTimestamp(LocalDateTime.now().toString());
        return new ResponseEntity<>(
                errorDTO,
                HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(NoHttpResponseException.class)
    public ResponseEntity<ErrorDTO> handleNoHttpResponseException(NoHttpResponseException ex, HttpServletRequest request) {
        if (StringUtils.isEmpty(ex.getMessage())) {
            Throwable throwable = ex.getCause();
            if (throwable instanceof AppGlobalException) {
                return this.getMessage(throwable, request);
            }
        }
        sendMessageToTelegramBot("NoHttpResponseException", ex.getMessage(), HttpStatus.REQUEST_TIMEOUT, request);
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setTitle(localizationService.localize(MessageConstants.ERROR_FROM_NIC));
        errorDTO.setDetail(localizationService.localize(MessageConstants.TIME_OUT_ERROR));
        errorDTO.setRefreshToken(true);
        errorDTO.setStatus(HttpStatus.REQUEST_TIMEOUT.value());
        errorDTO.setPath(request.getRequestURI());
        errorDTO.setTimestamp(LocalDateTime.now().toString());
        return new ResponseEntity<>(
                errorDTO,
                HttpStatus.REQUEST_TIMEOUT);
    }

    @ExceptionHandler(HttpClientErrorException.TooManyRequests.class)
    public ResponseEntity<ErrorDTO> handlerTooManyRequestException(HttpClientErrorException.TooManyRequests ex, HttpServletRequest request) {
        if (StringUtils.isEmpty(ex.getMessage())) {
            Throwable throwable = ex.getCause();
            if (throwable instanceof AppGlobalException) {
                return this.getMessage(throwable, request);
            }
        }
        sendMessageToTelegramBot("HttpClientErrorException.TooManyRequests", ex.getMessage(), HttpStatus.TOO_MANY_REQUESTS, request);
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setTitle(localizationService.localize(MessageConstants.ASL_BELGI_ERROR));
        errorDTO.setDetail(ex.getMessage());
        errorDTO.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        errorDTO.setPath(request.getRequestURI());
        errorDTO.setTimestamp(LocalDateTime.now().toString());
        return new ResponseEntity<>(
                errorDTO,
                HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<ErrorDTO> handleInvalidFormatException(InvalidFormatException ex, HttpServletRequest request) {
        if (StringUtils.isEmpty(ex.getMessage())) {
            Throwable throwable = ex.getCause();
            if (throwable instanceof AppGlobalException) {
                return this.getMessage(throwable, request);
            }
        }
        sendMessageToTelegramBot("InvalidFormatException", ex.getMessage(), HttpStatus.BAD_REQUEST, request);
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setTitle(localizationService.localize(MessageConstants.ERROR));
        errorDTO.setDetail(ex.getMessage());
        errorDTO.setStatus(HttpStatus.BAD_REQUEST.value());
        errorDTO.setPath(request.getRequestURI());
        errorDTO.setTimestamp(LocalDateTime.now().toString());
        return ResponseEntity.badRequest().body(errorDTO);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        sendMessageToTelegramBot("MethodArgumentNotValidException", ex.getMessage(), HttpStatus.BAD_REQUEST, request);
        BindingResult result = ex.getBindingResult();
        List<FieldErrorVM> fieldErrors = result
                .getFieldErrors()
                .stream()
                .map(field ->
                        new FieldErrorVM(
                                field.getObjectName().replaceFirst("DTO$", ""),
                                field.getField(),
                                localizationService.localize(MessageConstants.REQUIRED, new Object[]{localizationService.localize(field.getField())})
                        )
                )
                .toList();

        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setTitle(localizationService.localize(MessageConstants.FILL_IN_ALL_REQUIRED_FIELDS));
        errorDTO.setStatus(HttpStatus.BAD_REQUEST.value());
        errorDTO.setPath(request.getRequestURI());
        errorDTO.setTimestamp(LocalDateTime.now().toString());
        errorDTO.setErrorFields(fieldErrors);
        return ResponseEntity.badRequest().body(errorDTO);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorDTO> validationExceptionHandler(BindException ex, HttpServletRequest request) {
        if (StringUtils.isEmpty(ex.getMessage())) {
            Throwable throwable = ex.getCause();
            if (throwable instanceof AppGlobalException) {
                return this.getMessage(throwable, request);
            }
        }
        sendMessageToTelegramBot("BindException", ex.getMessage(), HttpStatus.BAD_REQUEST, request);

        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setTitle(ex.getMessage());
        errorDTO.setDetail(ex.getMessage());
        errorDTO.setStatus(HttpStatus.BAD_REQUEST.value());
        errorDTO.setPath(request.getRequestURI());
        errorDTO.setTimestamp(LocalDateTime.now().toString());
        return ResponseEntity.badRequest().body(errorDTO);
    }

    private ResponseEntity<ErrorDTO> getMessage(Throwable throwable, HttpServletRequest request) {
        AppGlobalException appGlobalException = (AppGlobalException) throwable;
        sendMessageToTelegramBot(appGlobalException.getTitle(), appGlobalException.getMessage(), appGlobalException.getStatus(), request);
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setTitle(appGlobalException.getTitle());
        errorDTO.setDetail(appGlobalException.getMessage());
        errorDTO.setStatus(appGlobalException.getStatus().value());
        errorDTO.setPath(request.getRequestURI());
        errorDTO.setTimestamp(LocalDateTime.now().toString());
        return new ResponseEntity<>(
                errorDTO,
                appGlobalException.status);
    }

    private void sendMessageToTelegramBot(String title, String message, HttpStatus status, HttpServletRequest request) {
        this.sendMessageToTelegramBot(title, message, status, request, null);
    }

    private void sendMessageToTelegramBot(String title, String message, HttpStatus status, HttpServletRequest request, String entityId) {
        String user = null;
        if (request.toString().contains("user=")) {
            user = request.toString().split("user=")[1];
        } else if (request.getRemoteUser() != null)
            user = request.getRemoteUser();

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
        if (StringUtils.isNotEmpty(SecurityUtils.getCurrentUserIP())) {
            messageBuilder.append("<b>IP Address:</b> ").append(SecurityUtils.getCurrentUserIP()).append("\n");
        }
        if (StringUtils.isNotEmpty(user)) {
            messageBuilder.append("<b>User:</b> ").append(user).append("\n");
        }
        if (StringUtils.isNotEmpty(SecurityUtils.getCurrentCompanyTin())) {
            messageBuilder.append("<b>Company TIN:</b> ").append(SecurityUtils.getCurrentCompanyTin()).append("\n");
        }
        messageBuilder.append("<b>Date:</b> ").append(LocalDateTime.now()).append("\n");
        messageBuilder.append("<b>Title:</b> ").append(title).append("\n");
        messageBuilder.append("<b>Message:</b> ").append(message).append("\n");

        telegramApplicationLogService.send(messageBuilder.toString());
        log.error(messageBuilder.toString());
    }
}
