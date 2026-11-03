package uz.uzkassa.silen.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import uz.uzkassa.silen.config.ApplicationProperties;
import uz.uzkassa.silen.enumeration.UserRole;
import uz.uzkassa.silen.security.CustomUser;

import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProvider implements InitializingBean {
    private static final String AUTHORITIES_KEY = "auth";
    private long tokenValidityInMilliseconds;
    private long lineTokenValidityInMilliseconds;
    private final ApplicationProperties applicationProperties;
    private Algorithm key;

    @Override
    public void afterPropertiesSet() {
        log.debug("Using a Base64-encoded JWT secret key");
        this.tokenValidityInMilliseconds = 1000 * applicationProperties.getTokenValidityInSeconds();
        this.lineTokenValidityInMilliseconds = 1000 * applicationProperties.getLineTokenValidityInSeconds();
        this.key = Algorithm.HMAC512(Base64.getDecoder().decode(applicationProperties.getJwtSecretKey()));
    }

    public String createToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));

        String userId = null;
        String branchId = null;
        String organizationId = null;
        String organizationTin = null;

        if (authentication.getPrincipal() instanceof CustomUser customUser) {
            userId = customUser.getUserId();
            branchId = customUser.getBranchId();
            organizationId = customUser.getOrganizationId();
            organizationTin = customUser.getOrganizationTin();
        }
        boolean isLine = authentication.getAuthorities().contains(new SimpleGrantedAuthority(UserRole.LINE.getCode()));
        long now = (new Date()).getTime() + (isLine ? this.lineTokenValidityInMilliseconds : this.tokenValidityInMilliseconds);
        Date validity = new Date(now);
        return JWT.create()
            .withSubject(authentication.getName())
            .withClaim(AUTHORITIES_KEY, authorities)
            .withClaim("userId", userId)
            .withClaim("branchId", branchId)
            .withClaim("organizationId", organizationId)
            .withClaim("organizationTin", organizationTin)
            .withClaim("sessionId", System.currentTimeMillis())
            .withExpiresAt(validity)
            .sign(key);
    }

    public Authentication getAuthentication(String token) {
        DecodedJWT decodedJWT = JWT.require(key)
            .build()
            .verify(token);

        final String grantedAuthorities = decodedJWT.getClaim(AUTHORITIES_KEY).asString();
        final Collection<? extends GrantedAuthority> authorities =
            Arrays.stream(grantedAuthorities.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        final String userId = decodedJWT.getClaim("userId").isMissing() ? null : decodedJWT.getClaim("userId").asString();
        final String branchId = decodedJWT.getClaim("branchId").isMissing() ? null : decodedJWT.getClaim("branchId").asString();
        final String organizationId = decodedJWT.getClaim("organizationId").isMissing() ? null : decodedJWT.getClaim("organizationId").asString();
        final String organizationTin = decodedJWT.getClaim("organizationTin").isMissing() ? null : decodedJWT.getClaim("organizationTin").asString();
        final Long sessionId = decodedJWT.getClaim("sessionId").isMissing() ? null : decodedJWT.getClaim("sessionId").asLong();

        final CustomUser principal = new CustomUser(decodedJWT.getSubject(), "", authorities, userId, branchId, organizationId, organizationTin, sessionId);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public Authentication getAuthenticationBySecretKey() {
        final String userId = applicationProperties.getLineUserId();
        final String organizationId = applicationProperties.getLineOrganizationId();
        final String organizationTin = applicationProperties.getLineOrganizationTin();
        final Long sessionId = 111_111_111L;
        final CustomUser principal = new CustomUser("SILENRADIKS", "", Collections.emptyList(), userId, null, organizationId, organizationTin, sessionId);
        return new UsernamePasswordAuthenticationToken(principal, null, Collections.emptyList());
    }

    public boolean validateToken(final String authToken) {
        try {
            JWT.require(key).build()
                .verify(authToken);
            return true;
        } catch (final JWTVerificationException | IllegalArgumentException e) {
            log.info("Invalid JWT token.");
            log.trace("Invalid JWT token trace.", e);
        }
        return false;
    }
}
