package uz.uzkassa.silen.exceptions;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import uz.uzkassa.silen.security.SecurityUtils;
import uz.uzkassa.silen.security.UserNotActivatedException;
import uz.uzkassa.silen.service.LocalizationService;
import uz.uzkassa.silen.service.TelegramBotApi;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionTranslator {
    private final TelegramBotApi botApi;
    private final LocalizationService localizationService;
    private static final List<String> ignoreByUrl = List.of("/api/aggregation/validate", "/api/aggregation/block/validate", "/api/invoice/webhook/updated","/api/aggregation/histories/check");

    @ResponseBody
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<GlobalExceptionResponse> handleAccessDeniedException(final AccessDeniedException ex, final HttpServletRequest request) {
        final GlobalExceptionResponse response = GlobalExceptionResponse.builder().title(ex.getLocalizedMessage()).status(HttpStatus.FORBIDDEN.value()).detail(ex.getMessage()).path(request.getRequestURI()).message(ex.getMessage()).code(ErrorConstants.ERR_VALIDATION).errorCode(ErrorConstants.DEFAULT_ERROR_CODE).build();
        return handleSimple(response);
    }

    @ResponseBody
    @ExceptionHandler(ConcurrencyFailureException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<GlobalExceptionResponse> handleConcurrencyFailureException(final ConcurrencyFailureException ex, final HttpServletRequest request) {
        final GlobalExceptionResponse response = GlobalExceptionResponse.builder().title(ex.getLocalizedMessage()).status(HttpStatus.CONFLICT.value()).detail(ex.getMessage()).path(request.getRequestURI()).message(ex.getMessage()).code(ErrorConstants.ERR_VALIDATION).errorCode(ErrorConstants.DEFAULT_ERROR_CODE).build();
        return handle(response, request);
    }

    @ResponseBody
    @ExceptionHandler(UserNotActivatedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<GlobalExceptionResponse> processUserNotActivatedException(final UserNotActivatedException ex, final HttpServletRequest request) {
        final GlobalExceptionResponse response = GlobalExceptionResponse.builder().title(ex.getLocalizedMessage()).status(HttpStatus.UNAUTHORIZED.value()).detail(ex.getMessage()).path(request.getRequestURI()).message(ex.getMessage()).code(ErrorConstants.ERR_CREDENTIALS).errorCode(ErrorConstants.DEFAULT_ERROR_CODE).build();
        return handle(response, request);
    }

    @ResponseBody
    @ExceptionHandler(InternalAuthenticationServiceException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<GlobalExceptionResponse> handleInternalAuthenticationServiceException(final InternalAuthenticationServiceException ex, final HttpServletRequest request) {
        final GlobalExceptionResponse response = GlobalExceptionResponse.builder().title(ex.getLocalizedMessage()).status(HttpStatus.UNAUTHORIZED.value()).detail(ex.getMessage()).path(request.getRequestURI()).message(ex.getMessage()).code(ErrorConstants.ERR_CREDENTIALS).errorCode(ErrorConstants.DEFAULT_ERROR_CODE).build();
        return handleSimple(response);
    }

    @ResponseBody
    @ExceptionHandler(AccountExpiredException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<GlobalExceptionResponse> handleAccountExpiredException(final AccountExpiredException ex, final HttpServletRequest request) {
        final GlobalExceptionResponse response = GlobalExceptionResponse.builder().title(ex.getLocalizedMessage()).status(HttpStatus.UNAUTHORIZED.value()).detail(ex.getMessage()).path(request.getRequestURI()).message(ex.getMessage()).code(ErrorConstants.ERR_CREDENTIALS).errorCode(ErrorConstants.DEFAULT_ERROR_CODE).build();
        return handleSimple(response);
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<GlobalExceptionResponse> handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException ex, final HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        map.put(ex.getName(), "Invalid argument type");
        final GlobalExceptionResponse response = GlobalExceptionResponse.builder().title(ex.getLocalizedMessage()).status(HttpStatus.BAD_REQUEST.value()).detail(ex.getMessage()).path(request.getRequestURI()).message(ex.getMessage()).code(ErrorConstants.ERR_VALIDATION).errorCode(ErrorConstants.DEFAULT_ERROR_CODE).errors(map).build();
        return handle(response, request);
    }

    @ResponseBody
    @ExceptionHandler(JsonParseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<GlobalExceptionResponse> handleJsonParseException(final JsonParseException ex, final HttpServletRequest request) {
        final GlobalExceptionResponse response = GlobalExceptionResponse.builder().title(ex.getLocalizedMessage()).status(HttpStatus.BAD_REQUEST.value()).detail(ex.getMessage()).path(request.getRequestURI()).message("Can not deserialize http request body").code(ErrorConstants.ERR_INTERNAL_DESERIALIZATION).errorCode(ErrorConstants.DEFAULT_ERROR_CODE).build();
        return handle(response, request);
    }

    @ResponseBody
    @ExceptionHandler(JsonMappingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<GlobalExceptionResponse> handleJsonMappingException(final JsonMappingException ex, final HttpServletRequest request) {
        final GlobalExceptionResponse response = GlobalExceptionResponse.builder().title(ex.getLocalizedMessage()).status(HttpStatus.BAD_REQUEST.value()).detail(ex.getMessage()).path(request.getRequestURI()).message("Can not deserialize http request body").code(ErrorConstants.ERR_INTERNAL_DESERIALIZATION).errorCode(ErrorConstants.DEFAULT_ERROR_CODE).build();
        return handle(response, request);
    }

    @ResponseBody
    @ExceptionHandler(UnrecognizedPropertyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<GlobalExceptionResponse> handleUnrecognizedPropertyException(final UnrecognizedPropertyException ex, final HttpServletRequest request) {
        final GlobalExceptionResponse response = GlobalExceptionResponse.builder().title(ex.getLocalizedMessage()).status(HttpStatus.BAD_REQUEST.value()).detail(ex.getMessage()).path(request.getRequestURI()).message("Found unrecognized property").code(ErrorConstants.ERR_INTERNAL_UNRECOGNIZED).errorCode(ErrorConstants.DEFAULT_ERROR_CODE).errors(new HashMap<>() {{
            this.put(ex.getPropertyName(), ex.getMessage());
        }}).build();
        return handle(response, request);
    }

    @ResponseBody
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<GlobalExceptionResponse> handleBadRequestException(final BadRequestException ex, final HttpServletRequest request) {
        final GlobalExceptionResponse response = GlobalExceptionResponse.builder().title(ex.getLocalizedMessage()).status(HttpStatus.BAD_REQUEST.value()).detail(ex.getMessage()).path(request.getRequestURI()).message(ex.getMessage()).code(ex.getCode()).errorCode(ex.getErrorCode())
                .errors(ex.getErrors()).build();
        return handleSimple(response);
    }

    @ResponseBody
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<GlobalExceptionResponse> handleEntityNotFoundException(final EntityNotFoundException ex, final HttpServletRequest request) {
        final GlobalExceptionResponse response = GlobalExceptionResponse.builder().title(ex.getLocalizedMessage()).status(HttpStatus.NOT_FOUND.value()).detail(ex.getMessage()).path(request.getRequestURI()).message(ex.getMessage()).code(ex.getCode()).errorCode(ex.getErrorCode()).build();
        return handleSimple(response);
    }

    @ResponseBody
    @ExceptionHandler(RestException.class)
    public ResponseEntity<GlobalExceptionResponse> handleRestException(final RestException ex, final HttpServletRequest request) {
        final ResponseStatus responseStatus = AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class);
        final GlobalExceptionResponse response = GlobalExceptionResponse.builder().title(ex.getLocalizedMessage()).status(Optional.ofNullable(responseStatus).map(s -> s.value().value()).orElseGet(HttpStatus.INTERNAL_SERVER_ERROR::value)).detail(ex.getMessage()).path(request.getRequestURI()).message(ex.getMessage()).code(ex.getCode()).errorCode(ex.getErrorCode()).build();
        return handle(response, request);
    }

    @ResponseBody
    @ExceptionHandler(TuronRestException.class)
    public ResponseEntity<GlobalExceptionResponse> handleTuronRestException(final TuronRestException ex, final HttpServletRequest request) {
        final ResponseStatus responseStatus = AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class);
        final GlobalExceptionResponse response = GlobalExceptionResponse.builder().title(ex.getLocalizedMessage()).status(Optional.ofNullable(responseStatus).map(s -> s.value().value()).orElseGet(HttpStatus.INTERNAL_SERVER_ERROR::value)).detail(ex.getMessage()).path(request.getRequestURI()).message(ex.getMessage()).code(ex.getCode()).errorCode(ex.getErrorCode()).build();
        return handleSimple(response);
    }

    @ResponseBody
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<GlobalExceptionResponse> handleBadCredentialsException(final BadCredentialsException ex, final HttpServletRequest request) {
        final GlobalExceptionResponse response = GlobalExceptionResponse.builder().title(ex.getLocalizedMessage()).status(HttpStatus.UNAUTHORIZED.value()).detail(ex.getMessage()).path(request.getRequestURI()).message("Bad credentials").code(ErrorConstants.ERR_CREDENTIALS).errorCode(HttpStatus.UNAUTHORIZED.value()).build();
        return handleSimple(response);
    }

    @ResponseBody
    @ExceptionHandler(InsufficientAuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<GlobalExceptionResponse> handleInsufficientAuthenticationException(final InsufficientAuthenticationException ex, final HttpServletRequest request) {
        final GlobalExceptionResponse response = GlobalExceptionResponse.builder().title(ex.getLocalizedMessage()).status(HttpStatus.UNAUTHORIZED.value()).detail(ex.getMessage()).path(request.getRequestURI()).message("Token expired").code(ErrorConstants.ERR_CREDENTIALS).errorCode(HttpStatus.UNAUTHORIZED.value()).build();
        return handleSimple(response);
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalExceptionResponse> handleInsufficientAuthenticationException(final MethodArgumentNotValidException ex, final HttpServletRequest request) {
        Map<String, Object> exceptions = new HashMap<>();
        BindingResult results = ex.getBindingResult();
        StringBuilder stringBuilder = new StringBuilder(localizationService.getMessage("invalid.request.content"));
        for (FieldError f : results.getFieldErrors()) {
            exceptions.put(f.getField(), f.getDefaultMessage());
            stringBuilder.append(" - ");
            stringBuilder.append(localizationService.getMessage(f.getField(), f.getField()));
            stringBuilder.append(" : ");
            stringBuilder.append(f.getDefaultMessage());
            stringBuilder.append("\n");
        }

        final GlobalExceptionResponse response = GlobalExceptionResponse.builder().title(localizationService.getMessage("invalid.request.content")).status(HttpStatus.BAD_REQUEST.value()).detail(ErrorConstants.ERR_VALIDATION).path(request.getRequestURI()).message(stringBuilder.toString()).code(ErrorConstants.ERR_VALIDATION).errorCode(HttpStatus.BAD_REQUEST.value()).errors(exceptions).build();
        return handle(response, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalExceptionResponse> handleException(final Exception ex, final HttpServletRequest request) {
        final GlobalExceptionResponse response = GlobalExceptionResponse.builder().title(ex.getLocalizedMessage()).status(HttpStatus.INTERNAL_SERVER_ERROR.value()).detail(ex.getMessage()).path(request.getRequestURI()).message("Internal server error").code(ErrorConstants.ERR_INTERNAL_SERVER_ERROR).errorCode(500).build();
        log.error("Unhandled exception", ex);
        return handle(response, request);
    }

    public ResponseEntity<GlobalExceptionResponse> handle(final GlobalExceptionResponse problem, final HttpServletRequest request) {
        final String path = problem.getPath();
        final String method = request.getMethod();

        if (HttpStatus.UNAUTHORIZED.value() != problem.getStatus() && !path.contains("/api/admin/billing-services/")) {
           new Thread(() -> sendMessageToTelegramBot(path, problem, method)).start();
        }

        return ResponseEntity.status(problem.getStatus()).body(problem);
    }

    public ResponseEntity<GlobalExceptionResponse> handleSimple(final GlobalExceptionResponse problem) {
        return ResponseEntity.status(problem.getStatus()).body(problem);
    }

    private void sendMessageToTelegramBot(final String apiUrl, final GlobalExceptionResponse problem, final String method) {
        if (ignoreByUrl.contains(apiUrl)) {
            return;
        }
        long start = System.currentTimeMillis();
        final StringBuilder text = new StringBuilder();
        text.append("<b>Type:</b> ").append(HttpStatus.valueOf(problem.getStatus())).append("\n");
        text.append("<b>Status:</b> ").append(problem.getStatus()).append("\n");
        if (StringUtils.isNotEmpty(method)) {
            text.append("<b>Method:</b> ").append(method).append("\n");
        }
        if (StringUtils.isNotEmpty(apiUrl)) {
            text.append("<b>API:</b> ").append(apiUrl).append("\n");
        }
        if (StringUtils.isNotEmpty(SecurityUtils.getCurrentOrganizationTin())) {
            text.append("<b>Tin:</b> ").append(SecurityUtils.getCurrentOrganizationTin()).append("\n");
        }
        if (StringUtils.isNotEmpty(SecurityUtils.getCurrentUserLogin())) {
            text.append("<b>User:</b> ").append(SecurityUtils.getCurrentUserLogin()).append("\n");
        }
        text.append("<b>Date:</b> ").append(LocalDateTime.now()).append("\n");
        if (StringUtils.isNotEmpty(problem.getTitle())) {
            text.append("<b>Title:</b> ").append(problem.getTitle()).append("\n");
        }
        if (StringUtils.isNotEmpty(problem.getDetail())) {
            text.append("<b>Message:</b> ").append(problem.getDetail()).append("\n");
        }
        if (!CollectionUtils.isEmpty(problem.getErrors())) {
            text.append("<b>Errors: </b> ").append(problem.getErrors()).append("\n");
        }
        if ("Method argument not valid".equals(problem.getTitle())) {
            text.append("<b>Problem:</b> ").append(problem);
        }

        try {
            botApi.sendMessageToAdminChannel(text.toString());
            log.info("message sending error executed: {}ms", (System.currentTimeMillis() - start));
        } catch (Exception e) {
            log.error("message sending error executed: {}ms", (System.currentTimeMillis() - start), e);
        }
    }
}
