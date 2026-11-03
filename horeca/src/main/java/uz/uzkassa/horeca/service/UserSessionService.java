package uz.uzkassa.horeca.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.horeca.domain.auth.User;
import uz.uzkassa.horeca.domain.auth.UserSession;
import uz.uzkassa.horeca.dto.session.UserSessionDTO;
import uz.uzkassa.horeca.repository.UserSessionRepository;

import java.time.LocalDateTime;

/**
 * Author: Shuxratjon Rayimjonov
 * Date: 30.10.2024
 */

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserSessionService extends BaseService {

    UserSessionRepository userSessionRepository;

    @Transactional
    public void create(UserSessionDTO userSessionDTO) {

        User currentUser = getCurrentUser();
        UserSession userSession = userSessionRepository.findFirstByFcmToken(userSessionDTO.getFcmToken()).orElseGet(UserSession::new);

        if (userSession.getId() != null) {
            if (!userSession.getUserId().equals(currentUser.getId())) {
                userSessionRepository.delete(userSession);
                UserSession newSession = new UserSession();
                newSession.setFcmToken(userSessionDTO.getFcmToken());
                newSession.setUserId(currentUser.getId());
                newSession.setLastUpdatedDate(LocalDateTime.now());
                userSessionRepository.save(newSession);
            }
        } else {
            userSession.setFcmToken(userSessionDTO.getFcmToken());
            userSession.setUserId(currentUser.getId());
            userSessionRepository.save(userSession);
        }
    }

    @Transactional
    public void delete(UserSessionDTO userSessionDTO) {

        User currentUser = getCurrentUser();
        UserSession userSession = userSessionRepository.findFirstByFcmToken(userSessionDTO.getFcmToken()).orElseThrow(notFoundExceptionThrow(UserSession.class.getSimpleName(),"fcmToken",userSessionDTO.getFcmToken()));
        if (userSession.getUserId().equals(currentUser.getId())) {
            userSessionRepository.delete(userSession);
        }
    }
}
