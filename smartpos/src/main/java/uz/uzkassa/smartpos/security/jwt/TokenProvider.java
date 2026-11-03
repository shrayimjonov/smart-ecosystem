package uz.uzkassa.smartpos.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpos.config.ApplicationProperties;
import uz.uzkassa.smartpos.domain.postgres.user.Authority;
import uz.uzkassa.smartpos.domain.postgres.user.User;
import uz.uzkassa.smartpos.security.CustomUser;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProvider implements InitializingBean {
    private static final String AUTHORITIES_KEY = "auth";
    private long tokenValidityInMillis;
    private long tokenValidityRememberMeInMillis;
    private final ApplicationProperties applicationProperties;
    private Algorithm key;

    @Override
    public void afterPropertiesSet() {
        log.debug("Using a Base64-encoded JWT secret key");
        this.tokenValidityInMillis = 1000 * applicationProperties.getTokenValidityInSeconds();
        this.tokenValidityRememberMeInMillis = 1000 * applicationProperties.getTokenValidityInSecondsRememberMe();
        this.key = Algorithm.HMAC512(Base64.getDecoder().decode(applicationProperties.getJwtSecretKey()));
    }

    public String createToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        Long userId = null;
        Long companyId = null;
        Long branchId = null;
        Long branchGroupId = null;

        if (authentication.getPrincipal() instanceof CustomUser customUser) {
            userId = customUser.getUserId();
            branchId = customUser.getBranchId();
            companyId = customUser.getCompanyId();
            branchGroupId = customUser.getBranchGroupId();
        }

        return JWT.create()
                .withSubject(authentication.getName())
                .withClaim(AUTHORITIES_KEY, authorities)
                .withClaim("userId", userId)
                .withClaim("branchId", branchId)
                .withClaim("branchGroupId", branchGroupId)
                .withClaim("companyId", companyId)
                .withExpiresAt(nextExpireTime(false))
                .sign(key);
    }

    public String createToken(CustomUser customUser, boolean forMobile) {
        String authorities = customUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        Long userId = customUser.getUserId();
        Long companyId = customUser.getCompanyId();
        Long branchId = customUser.getBranchId();
        Long branchGroupId = customUser.getBranchGroupId();

        return JWT.create()
                .withSubject(customUser.getUsername())
                .withClaim(AUTHORITIES_KEY, authorities)
                .withClaim("userId", userId)
                .withClaim("branchId", branchId)
                .withClaim("branchGroupId", branchGroupId)
                .withClaim("companyId", companyId)
                .withExpiresAt(nextExpireTime(forMobile))
                .sign(key);
    }

    public String createToken(User user) {
        String authorities = user.getAuthorities().stream()
                .map(Authority::getName)
                .collect(Collectors.joining(","));
        Long userId = user.getId();
        Long companyId = user.getCompanyId();
        Long branchId = user.getBranchId();
        Long branchGroupId = user.getBranchGroupId();

        return JWT.create()
                .withSubject(user.getLogin())
                .withClaim(AUTHORITIES_KEY, authorities)
                .withClaim("userId", userId)
                .withClaim("branchId", branchId)
                .withClaim("branchGroupId", branchGroupId)
                .withClaim("companyId", companyId)
                .withExpiresAt(nextExpireTime(false))
                .sign(key);
    }

    public Authentication getAuthentication(final String token) {
        DecodedJWT decodedJWT = JWT.require(key)
                .build()
                .verify(token);
        String grantedAuthorities = decodedJWT.getClaim(AUTHORITIES_KEY).asString();
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(grantedAuthorities.split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());


        Long userId = decodedJWT.getClaim("userId").isMissing() ? 0L : decodedJWT.getClaim("userId").asLong();
        Long branchId = decodedJWT.getClaim("branchId").isMissing() ? null : Optional.ofNullable(decodedJWT.getClaim("branchId")).map(Claim::asLong).orElse(null);
        Long branchGroupId = decodedJWT.getClaim("branchGroupId").isMissing() ? null : Optional.ofNullable(decodedJWT.getClaim("branchGroupId")).map(Claim::asLong).orElse(null);
        Long companyId = decodedJWT.getClaim("companyId").isMissing() ? 0L : decodedJWT.getClaim("companyId").asLong();

        CustomUser customUser = new CustomUser(decodedJWT.getSubject(), "", authorities, userId, companyId, branchId, branchGroupId);
        return new UsernamePasswordAuthenticationToken(customUser, token, authorities);
    }

    public boolean validateToken(String authToken) {
        try {
            JWT.require(key).build()
                    .verify(authToken);
            return true;
        } catch (Exception ignored) {
        }
        return false;
    }

    private Date nextExpireTime(boolean forMobile) {
        long now = (new Date()).getTime();
        return new Date(now + (forMobile ? tokenValidityRememberMeInMillis : tokenValidityInMillis));
    }
}
