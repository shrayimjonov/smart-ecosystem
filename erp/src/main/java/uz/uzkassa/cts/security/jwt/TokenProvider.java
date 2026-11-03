package uz.uzkassa.cts.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.config.ApplicationProperties;
import uz.uzkassa.cts.constants.AuthConstants;
import uz.uzkassa.cts.enums.Language;
import uz.uzkassa.cts.security.CustomUser;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class TokenProvider implements InitializingBean, AuthConstants {

    private SecretKey key;
    private final ApplicationProperties applicationProperties;

    @Override
    public void afterPropertiesSet() {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(applicationProperties.getJwtProperty().getSecretKey()));
    }

    @Transactional(readOnly = true)
    public String createToken(Authentication authentication, boolean rememberMe) {

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));


        long now = (new Date()).getTime();
        Date validity;
        if (rememberMe) {
            validity = new Date(now + this.applicationProperties.getJwtProperty().getTokenValidityInSecondsForRememberMe() * 1000);
        } else {
            validity = new Date(now + this.applicationProperties.getJwtProperty().getTokenValidityInSeconds() * 1000);
        }
        Long userId = null;
        Long companyId = null;
        Long branchId = null;
        Long departmentId = null;
        String language = null;
        String name = null;

        if (authentication.getPrincipal() instanceof CustomUser customUser) {
            userId = customUser.getUserId();
            companyId = customUser.getCompanyId();
            branchId = customUser.getBranchId();
            departmentId = customUser.getDepartmentId();
            language = customUser.getLanguage();
            name = customUser.getName();
        }

        return Jwts.builder()
                .subject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .claim(USER_ID, userId)
                .claim(COMPANY_ID, companyId)
                .claim(BRANCH_ID, branchId)
                .claim(DEPARTMENT_ID, departmentId)
                .claim(LANGUAGE, language)
                .claim(NAME, name)
                .signWith(key)
                .expiration(validity)
                .compact();
    }

    public Authentication getAuthentication(final String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        Long userId = claims.get(USER_ID) != null ? Long.valueOf((Integer) claims.get(USER_ID)) : null;
        Long companyId = claims.get(COMPANY_ID) != null ? Long.valueOf((Integer) claims.get(COMPANY_ID)) : null;
        Long branchId = claims.get(BRANCH_ID) != null ? Long.valueOf((Integer) claims.get(BRANCH_ID)) : null;
        Long departmentId = claims.get(DEPARTMENT_ID) != null ? Long.valueOf((Integer) claims.get(DEPARTMENT_ID)) : null;
        String language = (String) claims.getOrDefault(LANGUAGE, Language.ru.getCode());
        String status = (String) claims.getOrDefault("status", null);
        String name = (String) claims.getOrDefault(NAME, null);

        CustomUser principal = new CustomUser(claims.getSubject(), "", authorities, userId, companyId, branchId, departmentId, language, status, name);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.trace("Invalid JWT token trace.", e);
        }
        return false;
    }
}
