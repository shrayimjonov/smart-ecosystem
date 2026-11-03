package uz.uzkassa.silen.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.servlet.http.HttpServletRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uz.uzkassa.silen.domain.User;
import uz.uzkassa.silen.domain.redis.RedisLastAggregationMarks;
import uz.uzkassa.silen.domain.redis.RedisUser;
import uz.uzkassa.silen.dto.mq.NotificationPayload;
import uz.uzkassa.silen.dto.vm.LoginVM;
import uz.uzkassa.silen.enumeration.NotificationTemplateType;
import uz.uzkassa.silen.enumeration.UserRole;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.exceptions.EntityNotFoundException;
import uz.uzkassa.silen.rabbitmq.producer.RabbitMqProducer;
import uz.uzkassa.silen.repository.pg.UserRepository;
import uz.uzkassa.silen.repository.redis.RedisLastAggregationMarksRepository;
import uz.uzkassa.silen.repository.redis.RedisUserRepository;
import uz.uzkassa.silen.security.SecurityUtils;
import uz.uzkassa.silen.security.jwt.TokenProvider;
import uz.uzkassa.silen.transaction.AfterCommitExecutor;
import uz.uzkassa.silen.utils.DateUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthService {
    UserRepository userRepository;
    RedisUserRepository redisUserRepository;
    TokenProvider tokenProvider;
    AuthenticationManagerBuilder authenticationManagerBuilder;
    RabbitMqProducer rabbitMqProducer;
    AfterCommitExecutor afterCommitExecutor;
    RedisLastAggregationMarksRepository redisLastAggregationMarksRepository;

    public JWTToken logIn(final LoginVM loginVM, final HttpServletRequest request) {
        final String username = loginVM.getUsername();
        final User user = userRepository.findOneByLoginAndDeletedFalse(username)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        if (user.getRole() == UserRole.OPERATOR) {
            final Optional<RedisUser> redisUser = redisUserRepository.findById(user.getId());
            if (redisUser.isPresent()) {
                throw new BadRequestException("Авторизация не может быть выполнена. Оператор(" + username + ") уже зашел в систему в " + DateUtils.format(redisUser.get().getLogInDate()));
            }
        }

        final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, loginVM.getPassword());
        final Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final String jwt = tokenProvider.createToken(authentication);

        redisUserRepository.save(new RedisUser(user.getId(), LocalDateTime.now()));

        final NotificationPayload payload = new NotificationPayload();
        payload.addParams("userId", user.getId())
                .addParams("userName", username)
                .addParams("userIp", request.getRemoteAddr())
                .addParams("organizationTin", user.getOrganizationTin());
        payload.setOrganizationId(user.getOrganizationId());
        payload.setType(NotificationTemplateType.LOGIN);
        afterCommitExecutor.execute(() -> rabbitMqProducer.sendNotification(payload));

        return JWTToken.builder().idToken(jwt).build();
    }

    public void logOut() {
        if (SecurityUtils.getCurrentUserId() == null) return;
        redisUserRepository.findById(SecurityUtils.getCurrentUserId())
                .ifPresent(redisUserRepository::delete);

        Optional.ofNullable(SecurityUtils.getCurrentSessionId())
                .ifPresent(sessionId -> {
                    List<RedisLastAggregationMarks> allBySessionId = redisLastAggregationMarksRepository.findAllBySessionId(SecurityUtils.getCurrentSessionId());
//                Set<String> codes = allBySessionId.stream().map(RedisLastAggregationMarks::getPrintCode)
//                    .map(Utils::clearPrintCode).collect(Collectors.toSet());
                    // remove from even scanned marks
                    redisLastAggregationMarksRepository.deleteAll(allBySessionId);
                });
    }

    /**
     * Object to return as body in JWT Authentication.
     */
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    public static class JWTToken {
        @JsonProperty("id_token")
        private String idToken;
    }
}
