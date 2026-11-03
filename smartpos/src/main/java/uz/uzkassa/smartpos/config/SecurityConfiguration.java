package uz.uzkassa.smartpos.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import uz.uzkassa.smartpos.security.*;
import uz.uzkassa.smartpos.security.jwt.JWTFilter;

import java.time.Duration;
import java.util.List;

@AllArgsConstructor
@Configuration(proxyBeanMethods = false)
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final JWTFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOriginPatterns(List.of("*"));
                    config.setAllowedMethods(List.of("*"));
                    config.setExposedHeaders(List.of(HttpHeaders.AUTHORIZATION, HttpHeaders.LINK, "X-Total-Count"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    config.setMaxAge(Duration.ofSeconds(1800));
                    return config;
                }))
                .csrf(CsrfConfigurer::disable)
                .headers(headers ->
                                headers
//                                .contentSecurityPolicy(csp -> csp.policyDirectives(jHipsterProperties.getSecurity().getContentSecurityPolicy()))
                                        .frameOptions(FrameOptionsConfig::sameOrigin)
                                        .referrerPolicy(referrer -> referrer.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                                        .permissionsPolicy(permissions ->
                                                permissions.policy(
                                                        "camera=(), fullscreen=(self), geolocation=(), gyroscope=(), magnetometer=(), microphone=(), midi=(), payment=(), sync-xhr=()"
                                                )
                                        )
                )
                .authorizeHttpRequests(auth ->
                        // prettier-ignore
                        auth
                                .requestMatchers("/v3/api-docs/**", "/api-docs/**", "/swagger-ui/**",
                                        "/webjars/**", "/swagger-resources/**", "/swagger-ui.html",
                                        "/actuator/**").permitAll()
                                .requestMatchers("/api/authenticate", "/api/login", "/api/login/mobile", "/api/v1/terminals/login", "/api/v1/terminals/login/owner", "/api/v1/terminals/login/cashier", "/api/login/web", "/api/login/sign").permitAll()
                                .requestMatchers("/api/register-sms", "/api/register", "/api/activate-sms", "/api/refresh-token").permitAll()
                                .requestMatchers("/api/account/reset-password/init", "/api/account/reset-password/check", "/api/account/reset-password/finish").permitAll()
                                .requestMatchers("/api/v2/register", "/api/v2/activate", "/api/v2/reset-password", "/api/v2/forgot-password").permitAll()
                                .requestMatchers("/api/v1/terminals/users/roles").permitAll()
                                .requestMatchers("/api/v2/terminal/fiscalization/check").permitAll()
                                .requestMatchers("/api/telegram/web_hook").permitAll()
                                .requestMatchers("/api/invoice/webhook/**").permitAll()
                                .requestMatchers("/api/public/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/requisition").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/requisition/services").permitAll()
                                .requestMatchers(HttpMethod.OPTIONS, "/api/requisition").permitAll()
                                .requestMatchers("/api/billing-services/organization-application-form/create", "/api/billing-services/referral-link/check-referral-link").permitAll()
                                .requestMatchers(request -> (
                                        request.getRequestURI().startsWith("/api/billing") || request.getRequestURI().startsWith("/api/quote")) &&
                                        request.getMethod().equals(HttpMethod.GET.name())).hasAnyAuthority(AuthoritiesConstants.ADMIN_AND_BRANCH_ADMIN_ROLES.toArray(new String[0]))
                                .requestMatchers(request -> (
                                        request.getRequestURI().startsWith("/api/billing") || request.getRequestURI().startsWith("/api/quote")) &&
                                        !HttpMethod.GET.name().equals(request.getMethod())).hasAnyAuthority(AuthoritiesConstants.ADMIN_AND_OWNER_ROLES.toArray(new String[0]))
                                .requestMatchers("/api/v1/cabinet/**").hasAnyAuthority(AuthoritiesConstants.OWNER, AuthoritiesConstants.BRANCH_ADMIN)
                                .requestMatchers("/api/v1/admin/**").hasAnyAuthority(AuthoritiesConstants.ADMIN_ROLES.toArray(new String[0]))
                                .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions ->
                        exceptions
                                .authenticationEntryPoint(authenticationEntryPoint)
                                .accessDeniedHandler(accessDeniedHandler)
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(DomainUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(){
            @Override
            public boolean supports(Class<?> authentication) {
                return authentication.equals(UsernamePasswordAuthenticationToken.class);
            }
        };
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, DaoAuthenticationProvider authenticationProvider, SignatureAuthenticationProvider signatureAuthenticationProvider) throws Exception {
        AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class);
        auth.authenticationProvider(authenticationProvider);
        auth.authenticationProvider(signatureAuthenticationProvider);
       return auth.build();
    }
}
