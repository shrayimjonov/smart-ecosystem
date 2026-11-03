package uz.uzkassa.smartpossupply.web.rest.marketplace;

import io.swagger.v3.oas.annotations.Operation;
import uz.uzkassa.smartpossupply.constants.MessageConstants;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.dto.auth.MarketplaceActivationDTO;
import uz.uzkassa.smartpossupply.dto.auth.MarketplaceForgotPasswordDTO;
import uz.uzkassa.smartpossupply.dto.user.ResetPasswordInitDTO;
import uz.uzkassa.smartpossupply.dto.auth.CabinetLoginDTO;
import uz.uzkassa.smartpossupply.dto.auth.RefreshTokenDTO;
import uz.uzkassa.smartpossupply.dto.auth.TokenDTO;
import uz.uzkassa.smartpossupply.dto.user.SecretKeyDTO;
import uz.uzkassa.smartpossupply.security.jwt.TokenProvider;
import uz.uzkassa.smartpossupply.service.LocalizationService;
import uz.uzkassa.smartpossupply.service.UserService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.web.rest.errors.AppGlobalException;
import uz.uzkassa.smartpossupply.web.rest.errors.NotFoundException;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.marketplaceAccountRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MarketplaceAccountResource {

    TokenProvider tokenProvider;
    AuthenticationManagerBuilder authenticationManagerBuilder;
    UserService userService;
    LocalizationService localizationService;

    @PostMapping(ApiConstants.login)
    public ResponseEntity<TokenDTO> login(@Valid @RequestBody CabinetLoginDTO cabinetLoginDTO) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                cabinetLoginDTO.getPhone(),
                cabinetLoginDTO.getPassword()
        );
        try {
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String accessToken = tokenProvider.createToken(authentication, true);
            final String refreshToken = userService.getRefreshToken(cabinetLoginDTO.getPhone());
            TokenDTO tokenDTO = new TokenDTO(accessToken, refreshToken);
            HttpHeaders httpHeaders = SecurityUtils.getHeader();
            httpHeaders.setBearerAuth(tokenDTO.getAccessToken());
            return new ResponseEntity<>(tokenDTO, httpHeaders, HttpStatus.OK);
        } catch (AppGlobalException | AuthenticationException e) {
            if (e.getCause() instanceof NotFoundException) {
                throw new NotFoundException(localizationService.localize(MessageConstants.INCORRECT_USERNAME_OR_PASSWORD), e.getMessage());
            } else {
                throw new AppGlobalException(localizationService.localize(MessageConstants.INCORRECT_USERNAME_OR_PASSWORD), e.getMessage(), HttpStatus.UNAUTHORIZED);
            }
        }
    }

    @PostMapping(ApiConstants.refreshToken)
    public ResponseEntity<TokenDTO> refreshToken(@RequestBody @Valid RefreshTokenDTO refreshTokenDTO) {
        User user = userService.getUserByRefreshToken(refreshTokenDTO.refreshToken());
        final String accessToken = tokenProvider.createToken(user);
        HttpHeaders httpHeaders = SecurityUtils.getHeader();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        return new ResponseEntity<>(new TokenDTO(accessToken, user.getRefreshToken()), httpHeaders, HttpStatus.OK);
    }

    @Operation(summary = "Change Password Init")
    @PostMapping(ApiConstants.changePasswordInit)
    public ResponseEntity<Void> resetPasswordInit(@RequestBody ResetPasswordInitDTO resetPasswordInitDto) {
        userService.changePasswordInit(resetPasswordInitDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Change Password Check")
    @PostMapping(ApiConstants.changePasswordCheck)
    public ResponseEntity<SecretKeyDTO> resetPasswordCheck(@Valid @RequestBody MarketplaceActivationDTO marketplaceActivationDTO) {
        return ResponseEntity.ok(userService.changePasswordCheck(marketplaceActivationDTO));
    }

    @Operation(summary = "Change Password Finish")
    @PostMapping(ApiConstants.changePasswordFinish)
    public ResponseEntity<Void> resetPasswordFinish(@Valid @RequestBody MarketplaceForgotPasswordDTO marketplaceForgotPasswordDTO) {
        userService.changePasswordFinish(marketplaceForgotPasswordDTO);
        return ResponseEntity.ok().build();
    }

}
