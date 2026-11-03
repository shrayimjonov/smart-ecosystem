package uz.uzkassa.smartpos.exceptions;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import uz.uzkassa.smartpos.enums.HeaderKeys;
import uz.uzkassa.smartpos.security.HeaderUtils;
import uz.uzkassa.smartpos.security.SecurityUtils;
import uz.uzkassa.smartpos.security.UserLockedException;
import uz.uzkassa.smartpos.security.UserNotActivatedException;
import uz.uzkassa.smartpos.service.LocalizationService;
import uz.uzkassa.smartpos.social.TelegramBotApi;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

@AllArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {
    private final TelegramBotApi telegramBotApi;
    private final LocalizationService localizationService;
    private final ObjectMapper objectMapper;

    public ResponseEntity<GlobalExceptionResponse> handle(GlobalExceptionResponse problem, Exception exception, HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        if (401 != problem.getStatus() && 404 != problem.getStatus() && (problem.getErrorCode() == null || 400 != problem.getErrorCode()) && !(exception instanceof BadCredentialsException)) {
            sendMessageToTelegramBot(path, problem, method);
        }
        if (500 == problem.getStatus()) {
            problem.setTitle(localizationService.getMessage("internal.server.error"));
            problem.setDetail(localizationService.getMessage("internal.server.error"));
            problem.setMessage(localizationService.getMessage("internal.server.error"));
        }
        return ResponseEntity
                .status(problem.getStatus())
                .body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalExceptionResponse> handle(Exception exception, HttpServletRequest request) {
        GlobalExceptionResponse response = GlobalExceptionResponse.builder()
                .title(exception.getMessage())
                .status(500)
                .detail(exception.getMessage())
                .path(request.getRequestURI())
                .message(exception.getMessage())
                .build();
        return handle(response, exception, request);
    }

    @ExceptionHandler(CompletionException.class)
    public ResponseEntity<GlobalExceptionResponse> handle(CompletionException exception, HttpServletRequest request) {
        if (exception.getCause() != null && exception.getCause() instanceof BadRequestException badRequestException) {
            return handle(badRequestException, request);
        }
        GlobalExceptionResponse response = GlobalExceptionResponse.builder()
                .title(exception.getMessage())
                .status(500)
                .detail(exception.getMessage())
                .path(request.getRequestURI())
                .message(exception.getMessage())
                .build();
        return handle(response, exception, request);
    }


    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<GlobalExceptionResponse> handle(InternalAuthenticationServiceException exception, HttpServletRequest request) {
        GlobalExceptionResponse response = GlobalExceptionResponse.builder()
                .title(exception.getMessage())
                .status(400)
                .message(exception.getMessage())
                .solution(localizationService.getMessage("user.not.activated.solution"))
                .detail(localizationService.getMessage("user.not.activated.solution"))
                .build();
        return handle(response, exception, request);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<GlobalExceptionResponse> handle(HttpMediaTypeNotSupportedException exception, HttpServletRequest request) {
        GlobalExceptionResponse response = GlobalExceptionResponse.builder()
                .title(exception.getMessage())
                .status(400)
                .message(exception.getMessage())
                .build();
        return handle(response, exception, request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<GlobalExceptionResponse> handle(AccessDeniedException exception, HttpServletRequest request) {
        GlobalExceptionResponse response = GlobalExceptionResponse.builder()
                .title("У вас нет разрешения для этого ресурса")
                .status(403)
                .detail("У вас нет разрешения для этого ресурса")
                .path(request.getRequestURI())
                .message("У вас нет разрешения для этого ресурса")
                .build();
        return handle(response, exception, request);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<GlobalExceptionResponse> handle(MissingServletRequestPartException exception, HttpServletRequest request) {
        GlobalExceptionResponse response = GlobalExceptionResponse.builder()
                .title("Вам необходимо загрузить необходимый файл")
                .status(400)
                .detail(exception.getMessage())
                .path(request.getRequestURI())
                .build();
        return handle(response, exception, request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<GlobalExceptionResponse> handle(HttpRequestMethodNotSupportedException exception, HttpServletRequest request) {
        GlobalExceptionResponse response = GlobalExceptionResponse.builder()
                .title("Этот метод http не поддерживается")
                .status(400)
                .detail(exception.getMessage())
                .path(request.getRequestURI())
                .build();
        return handle(response, exception, request);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<GlobalExceptionResponse> handle(EntityNotFoundException exception, HttpServletRequest request) {
        GlobalExceptionResponse response = GlobalExceptionResponse.builder()
                .title(exception.getMessage())
                .status(404)
                .detail(exception.getMessage())
                .path(request.getRequestURI())
                .message(exception.getMessage())
                .build();
        return handle(response, exception, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<GlobalExceptionResponse> handle(HttpMessageNotReadableException ex, HttpServletRequest request) {
        GlobalExceptionResponse problem = GlobalExceptionResponse.builder()
                .status(400)
                .title(ex.getMessage())
                .detail(ex.getMessage())
                .path(request.getRequestURI())
                .message(ex.getMessage())
                .build();
        return handle(problem, ex, request);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<GlobalExceptionResponse> handle(MissingServletRequestParameterException ex, HttpServletRequest request) {
        GlobalExceptionResponse problem = GlobalExceptionResponse.builder()
                .status(400)
                .title(localizationService.getMessage("bad.request"))
                .detail(ex.getMessage())
                .path(request.getRequestURI())
                .message(ex.getMessage())
                .build();
        return handle(problem, ex, request);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<GlobalExceptionResponse> handle(NoResourceFoundException ex, HttpServletRequest request) {
        GlobalExceptionResponse problem = GlobalExceptionResponse.builder()
                .status(404)
                .title(localizationService.getMessage("bad.request"))
                .detail(localizationService.getMessage("bad.request"))
                .path(request.getRequestURI())
                .message(localizationService.getMessage("bad.request"))
                .build();
        return handle(problem, ex, request);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<GlobalExceptionResponse> handle(TokenExpiredException ex, HttpServletRequest request) {
        GlobalExceptionResponse problem = GlobalExceptionResponse.builder()
                .status(401)
                .title(ex.getMessage())
                .detail(ex.getMessage())
                .path(request.getRequestURI())
                .message(ex.getMessage())
                .build();
        return handle(problem, ex, request);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<GlobalExceptionResponse> handle(AuthenticationException ex, HttpServletRequest request) {
        GlobalExceptionResponse problem = GlobalExceptionResponse.builder()
                .status(401)
                .title(localizationService.getMessage("user.blocked.error"))
                .detail(localizationService.getMessage("user.blocked.error"))
                .message(localizationService.getMessage("user.blocked.error"))
                .solution(localizationService.getMessage("user.blocked.error.solution"))
                .path(request.getRequestURI())
                .build();
        return handle(problem, ex, request);
    }


    @ExceptionHandler(UserNotActivatedException.class)
    public ResponseEntity<GlobalExceptionResponse> handle(UserNotActivatedException ex, HttpServletRequest request) {
        GlobalExceptionResponse problem = GlobalExceptionResponse.builder()
                .status(401)
                .title(ex.getMessage())
                .detail(ex.getMessage())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return handle(problem, ex, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalExceptionResponse> handle(MethodArgumentNotValidException ex, @Nonnull HttpServletRequest request) {

        GlobalExceptionResponse problem = GlobalExceptionResponse.builder()
                .title("Аргумент метода недействителен")
                .status(400)
                .message("Аргумент метода недействителен")
                .path(request.getRequestURI())
                .errors(ex.getBindingResult().getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, f -> f.getCode() != null ? f.getCode() : "null", (a, b) -> a + ", " + b)))
                .build();
        return handle(problem, ex, request);
    }

    @ExceptionHandler(BranchNotFoundException.class)
    public ResponseEntity<GlobalExceptionResponse> handle(BranchNotFoundException ex, @Nonnull HttpServletRequest request) {

        GlobalExceptionResponse problem = GlobalExceptionResponse.builder()
                .title(localizationService.getMessage("branch.not.found"))
                .status(400)
                .message(localizationService.getMessage("branch.not.found"))
                .path(request.getRequestURI())
                .solution(localizationService.getMessage("branch.not.found.solution"))
                .build();
        return handle(problem, ex, request);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<GlobalExceptionResponse> handle(UserNotFoundException ex, @Nonnull HttpServletRequest request) {

        GlobalExceptionResponse problem = GlobalExceptionResponse.builder()
                .title(ex.getMessage())
                .status(400)
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .solution(localizationService.getMessage("user.not.found.error.solution"))
                .build();
        return handle(problem, ex, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<GlobalExceptionResponse> handle(ConstraintViolationException ex, @Nonnull HttpServletRequest request) {

        GlobalExceptionResponse problem = GlobalExceptionResponse.builder()
                .title("Аргумент метода недействителен")
                .status(400)
                .path(request.getRequestURI())
                .message("Аргумент метода недействителен")
                .errors(ex.getConstraintViolations().stream()
                        .collect(Collectors.toMap(violation -> violation.getPropertyPath() + " " + violation.getMessage(), violation -> Objects.toString(violation.getInvalidValue()))))
                .build();
        return handle(problem, ex, request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<GlobalExceptionResponse> handle(DataIntegrityViolationException ex, @Nonnull HttpServletRequest request) {
        ex.getMostSpecificCause();
        GlobalExceptionResponse problem = GlobalExceptionResponse.builder()
                .title(ex.getMostSpecificCause().getMessage())
                .status(500)
                .path(request.getRequestURI())
                .message(ex.getMostSpecificCause().getMessage())
                .build();
        return handle(problem, ex, request);
    }


    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<GlobalExceptionResponse> handle(HandlerMethodValidationException ex, @Nonnull HttpServletRequest request) {

        GlobalExceptionResponse problem = GlobalExceptionResponse.builder()
                .title("Аргумент метода недействителен")
                .status(400)
                .message("Аргумент метода недействителен")
                .path(request.getRequestURI())
                .errors(Map.of("keys", ex.getAllErrors()))
                .build();
        return handle(problem, ex, request);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<GlobalExceptionResponse> handle(NoSuchElementException ex, HttpServletRequest request) {
        GlobalExceptionResponse problem = GlobalExceptionResponse.builder()
                .status(404)
                .message(ErrorConstants.ERR_ENTITY_NOT_FOUND)
                .build();
        return handle(problem, ex, request);
    }

    @ExceptionHandler(AccessDeniedExp.class)
    public ResponseEntity<GlobalExceptionResponse> handle(AccessDeniedExp ex, HttpServletRequest request) {
        GlobalExceptionResponse problem = GlobalExceptionResponse.builder()
                .message(ex.getMessage())
                .status(403)
                .detail(ex.getMessage())
                .message(ex.getMessage())
                .build();
        return handle(problem, ex, request);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<GlobalExceptionResponse> handle(BadCredentialsException ex, HttpServletRequest request) {
        GlobalExceptionResponse problem = GlobalExceptionResponse.builder()
                .status(400)
                .title(localizationService.getMessage("login.password.error"))
                .detail(localizationService.getMessage("login.password.error"))
                .path(request.getRequestURI())
                .message(localizationService.getMessage("login.password.error"))
                .solution(localizationService.getMessage("login.password.error.solution"))
                .build();
        return handle(problem, ex, request);
    }

    @ExceptionHandler(UserLockedException.class)
    public ResponseEntity<GlobalExceptionResponse> handle(UserLockedException ex, HttpServletRequest request) {
        GlobalExceptionResponse problem = GlobalExceptionResponse.builder()
                .status(400)
                .title(localizationService.getMessage("user.blocked.error"))
                .detail(localizationService.getMessage("user.blocked.error"))
                .path(request.getRequestURI())
                .message(localizationService.getMessage("user.blocked.error"))
                .solution(localizationService.getMessage("user.blocked.error.solution"))
                .build();
        return handle(problem, ex, request);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<GlobalExceptionResponse> handle(BadRequestException ex, HttpServletRequest request) {
        GlobalExceptionResponse problem = GlobalExceptionResponse.builder()
                .status(400)
                .title(ex.getMessage())
                .detail(ex.getMessage())
                .errorCode(ex.getErrorCode())
                .solution(ex.getSolution())
                .path(request.getRequestURI())
                .build();
        return handle(problem, ex, request);
    }

    @ExceptionHandler(EntityAlreadyExistException.class)
    public ResponseEntity<GlobalExceptionResponse> handle(EntityAlreadyExistException ex, HttpServletRequest request) {
        GlobalExceptionResponse problem = GlobalExceptionResponse.builder()
                .status(409)
                .title(ex.getTitle() != null ? ex.getTitle() : ex.getLocalizedMessage())
                .detail(ex.getMessage())
                .build();
        return handle(problem, ex, request);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<GlobalExceptionResponse> handle(ValidationException ex, HttpServletRequest request) {
        GlobalExceptionResponse problem = GlobalExceptionResponse.builder()
                .status(400)
                .title(ex.getLocalizedMessage())
                .detail(ex.getMessage())
                .build();
        return handle(problem, ex, request);
    }

    @ExceptionHandler(EntityNotFound.class)
    public ResponseEntity<GlobalExceptionResponse> handle(EntityNotFound ex, HttpServletRequest request) {
        GlobalExceptionResponse problem = GlobalExceptionResponse.builder()
                .status(404)
                .title(ex.getLocalizedMessage())
                .detail(ex.getMessage())
                .build();
        return handle(problem, ex, request);
    }

    @ExceptionHandler(IntegrationException.class)
    public ResponseEntity<GlobalExceptionResponse> handle(IntegrationException ex, HttpServletRequest request) {
        GlobalExceptionResponse problem;
        if (ex.getError() != null) {
            problem = GlobalExceptionResponse.builder()
                    .status(ex.getError().getStatus())
                    .title(ex.getError().getMessage())
                    .detail(ex.getError().getMessage())
                    .path(ex.getError().getPath())
                    .build();
        } else {
            problem = GlobalExceptionResponse.builder()
                    .status(503)
                    .title(ex.getMessage())
                    .detail(ex.getMessage())
                    .build();
        }
        return handle(problem, ex, request);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<GlobalExceptionResponse> handle(HttpClientErrorException ex, HttpServletRequest request) {
        try {
            Map<String, Object> response = objectMapper.readValue(ex.getResponseBodyAsString(StandardCharsets.UTF_8), new TypeReference<>() {
            });
            return handle(GlobalExceptionResponse.builder()
                            .title(String.valueOf(response.getOrDefault("title", "Произошла ошибка при запросе")))
                            .detail(String.valueOf(response.getOrDefault("detail", "Произошла ошибка при запросе")))
                            .status(ex.getStatusCode().value())
                            .build(),
                    ex,
                    request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMessageToTelegramBot(String apiUrl, GlobalExceptionResponse problem, String method) {
        StringBuilder text = new StringBuilder();
        text.append("<b>Type:</b> ").append(HttpStatus.valueOf(problem.getStatus())).append("\n");
        text.append("<b>Status:</b> ").append(problem.getStatus()).append("\n");
        if (StringUtils.isNotEmpty(method)) {
            text.append("<b>Method:</b> ").append(method).append("\n");
        }
        if (StringUtils.isNotEmpty(apiUrl)) {
            text.append("<b>API:</b> `").append(apiUrl).append("`\n");
        }
        if (HeaderUtils.getCurrentHeaderByKey(HeaderKeys.TERMINAL_SN) != null) {
            text.append("<b>Terminal:</b> `").append(HeaderUtils.getCurrentHeaderByKey(HeaderKeys.TERMINAL_SN)).append("`\n");
        }
        SecurityUtils.getCurrentUserLogin()
                .ifPresent(login -> text.append("<b>User:</b> `").append(login).append("`\n"));
        if (SecurityUtils.getCurrentUserId() != null) {
            text.append("<b>UserId:</b> `").append(SecurityUtils.getCurrentUserId()).append("`\n");
        }
        if (SecurityUtils.getCurrentCompanyId() != null) {
            text.append("<b>CompanyId:</b> `").append(SecurityUtils.getCurrentCompanyId()).append("`\n");
        }
        if (SecurityUtils.getCurrentBranchId() != null) {
            text.append("<b>BranchId:</b> `").append(SecurityUtils.getCurrentBranchId()).append("`\n");
        }
        text.append("<b>Date:</b> ").append(LocalDateTime.now()).append("\n");
        if (StringUtils.isNotEmpty(problem.getTitle())) {
            text.append("<b>Title:</b> ").append(problem.getTitle()).append("\n");
        }
        if (StringUtils.isNotEmpty(problem.getDetail())) {
            text.append("<b>Detail:</b> ").append(problem.getDetail()).append("\n");
        }
        if (StringUtils.isNotEmpty(problem.getMessage())) {
            text.append("<b>Message:</b> ").append(problem.getMessage()).append("\n");
        }
        if (!CollectionUtils.isEmpty(problem.getErrors())) {
            text.append("  <b>errors: </b> ").append(problem.getErrors());
        }
        telegramBotApi.sendMessageToAdminChannel(text.toString());
    }
}
