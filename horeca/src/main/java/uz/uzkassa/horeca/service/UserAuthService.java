package uz.uzkassa.horeca.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.horeca.domain.auth.UserAuth;
import uz.uzkassa.horeca.dto.auth.UserAuthDTO;
import uz.uzkassa.horeca.repository.UserAuthRepository;

import java.time.LocalDateTime;


@Service

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserAuthService {

    UserAuthRepository userAuthRepository;

    @Transactional
    public void create(UserAuthDTO userAuthDTO) {
        UserAuth userAuth = new UserAuth();
        userAuth.setLogin(userAuthDTO.getLogin());
        userAuth.setActivationCode(userAuthDTO.getActivationCode());
        userAuth.setSecretKey(userAuthDTO.getSecretKey());
        userAuth.setRefreshToken(userAuthDTO.getRefreshToken());
        userAuth.setExpiryDate(userAuthDTO.getExpiryDate() != null ? userAuthDTO.getExpiryDate() : LocalDateTime.now().plusMinutes(2));
        userAuthRepository.save(userAuth);
    }

    public UserAuth getUserAuth(String login, String activationCode) {
        return userAuthRepository.findFirstByLoginAndActivationCodeOrderByIdDesc(login, activationCode).orElse(null);
    }
}
