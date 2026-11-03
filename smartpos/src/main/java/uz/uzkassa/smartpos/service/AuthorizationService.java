package uz.uzkassa.smartpos.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uz.uzkassa.smartpos.domain.postgres.user.User;
import uz.uzkassa.smartpos.dto.integration.launcher.JWTToken;
import uz.uzkassa.smartpos.enums.Role;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.exceptions.UserNotFoundException;
import uz.uzkassa.smartpos.repository.postgres.user.UserRepository;
import uz.uzkassa.smartpos.security.CustomUser;
import uz.uzkassa.smartpos.security.UserNotActivatedException;
import uz.uzkassa.smartpos.security.jwt.JWTFilter;
import uz.uzkassa.smartpos.security.jwt.TokenProvider;
import uz.uzkassa.smartpos.web.rest.vm.LoginVM;
import uz.uzkassa.smartpos.web.rest.vm.RefreshTokenRequest;
import uz.uzkassa.smartpos.web.rest.vm.TokenDTO;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorizationService {
    private final TokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final UserRepository userRepository;
    private final LocalizationService localizationService;

    public ResponseEntity<TokenDTO> login(LoginVM loginVM) {
        String jwt;
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginVM.getUsername(), loginVM.getPassword());
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            jwt = tokenProvider.createToken(authentication);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
            userService.authenticationSuccessHandler(loginVM);
            return new ResponseEntity<>(new TokenDTO(jwt), httpHeaders, HttpStatus.OK);
        } catch (Exception e) {
            userService.authenticationFailureHandler(loginVM, e);
            throw e;
        }
    }


    public ResponseEntity<TokenDTO> loginMobile(LoginVM loginVM, List<Role> roles) {
        Optional<User> user = userRepository.findOneWithAuthoritiesByLogin(loginVM.getUsername());
        if (user.isEmpty()) {
            throw new UserNotFoundException(localizationService.getMessage("user.not.found", loginVM.getUsername()));
        }
        if (roles != null && user.get().getAuthorities().stream().noneMatch(authority -> roles.contains(Role.fromRoleCode(authority.getName())))) {
            throw new BadRequestException(localizationService.getMessage("user.not.owner", loginVM.getUsername()));
        }
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginVM.getUsername(), loginVM.getPassword());
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            if (authentication.getPrincipal() instanceof CustomUser customUser) {
                String access_token = tokenProvider.createToken(customUser, true);
                String refreshToken = userService.getRefreshToken(loginVM.getUsername());

                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + access_token);
                userService.authenticationSuccessHandler(loginVM);
                return new ResponseEntity<>(new TokenDTO(access_token, refreshToken), httpHeaders, HttpStatus.OK);
            } else {
                throw new UserNotActivatedException("User " + loginVM.getUsername() + " was not activated");
            }
        } catch (Exception e) {
            userService.authenticationFailureHandler(loginVM, e);
            throw e;
        }
    }

    public ResponseEntity<TokenDTO> refreshToken(@Valid RefreshTokenRequest tokenDTO) {
        User user = userService.getUserByRefreshToken(tokenDTO.refreshToken());
        String access_token = tokenProvider.createToken(user);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + access_token);
        return new ResponseEntity<>(new TokenDTO(access_token, user.getRefreshToken()), httpHeaders, HttpStatus.OK);
    }

    public ResponseEntity<JWTToken> authorize(@Valid LoginVM loginVM) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginVM.getUsername(), loginVM.getPassword());

            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.createToken(authentication);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
            userService.authenticationSuccessHandler(loginVM);
            return new ResponseEntity<>(new JWTToken(jwt), httpHeaders, HttpStatus.OK);
        } catch (Exception e) {
            userService.authenticationFailureHandler(loginVM, e);
            throw e;
        }
    }

    public ResponseEntity<TokenDTO> authenticate(Authentication authenticationToken) {
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        if (authentication.getPrincipal() instanceof CustomUser customUser) {
            String access_token = tokenProvider.createToken(customUser, false);
            String refreshToken = userService.getRefreshToken(customUser.getUsername());

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + access_token);
            return new ResponseEntity<>(new TokenDTO(access_token, refreshToken), httpHeaders, HttpStatus.OK);
        } else {
            throw new UserNotActivatedException("User " + authentication.getPrincipal() + " was not authenticated");
        }
    }
}
