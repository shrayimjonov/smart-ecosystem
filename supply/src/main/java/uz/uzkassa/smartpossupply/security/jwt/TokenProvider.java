package uz.uzkassa.smartpossupply.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.constants.AuthConstants;
import uz.uzkassa.smartpossupply.domain.Branch;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.dto.auth.MobileTokenDTO;
import uz.uzkassa.smartpossupply.enums.Role;
import uz.uzkassa.smartpossupply.property.JwtTokenProperties;
import uz.uzkassa.smartpossupply.security.UserAuth;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProvider implements InitializingBean, AuthConstants {

    private SecretKey key;
    private final JwtTokenProperties jwtTokenProperties;

    @Override
    public void afterPropertiesSet() {
        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtTokenProperties.secretKey()));
    }

    public String createToken(Authentication authentication, boolean rememberMe) {
        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

        Long userId = null;
        Long companyId = null;
        Long branchId = null;
        Set<Long> branchIds = null;
        Long warehouseId = null;
        String language = null;
        String tin = null;

        if (authentication.getPrincipal() instanceof UserAuth userAuth) {
            userId = userAuth.getUserId();
            branchId = userAuth.getBranchId();
            branchIds = userAuth.getBranchIds();
            warehouseId = userAuth.getWarehouseId();
            companyId = userAuth.getCompanyId();
            language = userAuth.getLanguage();
            tin = userAuth.getTin();
        }

        return createToken(authentication.getName(), authorities, rememberMe, userId, companyId, branchId, branchIds, warehouseId, language, tin);
    }

    public MobileTokenDTO createTokenForMobile(Authentication authentication, boolean rememberMe) {
        final String accessToken = createToken(authentication, rememberMe);
        return new MobileTokenDTO(accessToken, authentication.getAuthorities().stream().map(grantedAuthority -> Role.valueOf(grantedAuthority.getAuthority())).collect(Collectors.toSet()));
    }

    public String createToken(Authentication authentication, Long companyId, String tin, boolean rememberMe) {
        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

        Long userId = null;
        String language = null;

        if (authentication.getPrincipal() instanceof UserAuth userAuth) {
            userId = userAuth.getUserId();
            language = userAuth.getLanguage();
        }

        return createToken(authentication.getName(), authorities, rememberMe, userId, companyId, null, Set.of(), null, language, tin);
    }

    public String createToken(User user) {
        Set<Long> branchIds = user.getBranches() != null ? user.getBranches().stream().map(Branch::getId).collect(Collectors.toSet()) : null;
        String tin = user.getCompany() != null ? user.getCompany().getTin() : null;
        return createToken(user.getLogin(), user.getRole().getCode(), true, user.getId(), user.getCompanyId(), user.getBranchId(), branchIds, user.getWarehouseId(), user.getLanguage() != null ? user.getLanguage().name() : null, tin);
    }

    private String createToken(String username, String role, boolean rememberMe, Long userId, Long companyId, Long branchId, Set<Long> branchIds, Long warehouseId, String language, String tin) {
        log.info(
                """
                        \tUserId: {}
                        \tCompanyId: {}
                        \tBranchId: {}
                        \tBranchIds: {}
                        \tWarehouseId: {}
                        \tLanguage: {}
                        \tTIN: {}
                        \tLogin: {}
                        \tRoles: {}
                        """,
                userId,
                companyId,
                branchId,
                branchIds,
                warehouseId,
                language,
                tin,
                username,
                role
        );

        return Jwts.builder()
                .subject(username)
                .claim(AUTHORITIES_KEY, role)
                .claim(USER_ID, userId)
                .claim(COMPANY_ID, companyId)
                .claim(BRANCH_ID, branchId)
                .claim(BRANCH_IDS, branchIds)
                .claim(WAREHOUSE_ID, warehouseId)
                .claim(LANGUAGE, language)
                .claim(TIN, tin)
                .signWith(key)
                .expiration(getNextExpiration(rememberMe))
                .compact();
    }

    private Date getNextExpiration(boolean rememberMe) {
        long now = (new Date()).getTime();
        Date validity;
        if (rememberMe) {
            validity = new Date(now + jwtTokenProperties.tokenValidityInSecondsForRememberMe() * 1000);
        } else {
            validity = new Date(now + jwtTokenProperties.tokenValidityInSeconds() * 1000);
        }
        return validity;
    }

    public Authentication getAuthentication(final String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String grantedAuthorities = claims.get(AUTHORITIES_KEY).toString();
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(grantedAuthorities.split(","))
                        .map(SimpleGrantedAuthority::new)
                        .toList();

        Long userId = null;
        if (claims.containsKey(USER_ID)) {
            userId = Long.valueOf((Integer) claims.get(USER_ID));
        }
        Long companyId = null;
        if (claims.containsKey(COMPANY_ID)) {
            companyId = Long.valueOf((Integer) claims.get(COMPANY_ID));
        }
        Long branchId = null;
        if (claims.containsKey(BRANCH_ID)) {
            branchId = Long.valueOf((Integer) claims.get(BRANCH_ID));
        }
        Set<Long> branchIds = null;
        if (claims.containsKey(BRANCH_IDS)) {
            branchIds = CollectionUtils.isNotEmpty((Collection<?>) claims.get(BRANCH_IDS)) ?
                    Arrays.stream(claims.get(BRANCH_IDS).toString().replaceAll("[\\[\\]\\s]", "").split(","))
                    .map(Long::valueOf)
                    .collect(Collectors.toSet())
                    : null;
        }
        Long warehouseId = null;
        if (claims.containsKey(WAREHOUSE_ID)) {
            warehouseId = Long.valueOf((Integer) claims.get(WAREHOUSE_ID));
        }
        String language = null;
        if (claims.containsKey(LANGUAGE)) {
            language = (String) claims.get(LANGUAGE);
        }
        String tin = null;
        if (claims.containsKey(TIN)) {
            tin = (String) claims.get(TIN);
        }

        UserAuth customUser = new UserAuth(
                claims.getSubject(),
                "",
                authorities,
                userId,
                companyId,
                branchId,
                branchIds,
                warehouseId,
                language,
                tin
        );
        return new UsernamePasswordAuthenticationToken(customUser, token, authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.trace("Invalid JWT token trace.", e);
        }
        return false;
    }
}
