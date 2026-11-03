package uz.uzkassa.cts.config;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import uz.uzkassa.cts.enums.Role;
import uz.uzkassa.cts.security.CustomAccessDeniedHandler;
import uz.uzkassa.cts.security.CustomAuthenticationEntryPoint;
import uz.uzkassa.cts.security.DomainUserDetailsService;
import uz.uzkassa.cts.security.jwt.JWTFilter;

import java.time.Duration;
import java.util.List;

@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SecurityConfiguration {

    JWTFilter jwtFilter;
    CustomAuthenticationEntryPoint authenticationEntryPoint;
    CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
                "/v3/api-docs/**", "/api-docs/**", "/swagger-ui/**", "/i18n/**",
                "/webjars/**", "/swagger-resources/**", "/swagger-ui.html",
                "/actuator/**");
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        // @formatter:off
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
                    .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                    .referrerPolicy(referrer -> referrer.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                    .permissionsPolicy(permissions ->
                        permissions.policy(
                            "camera=(), fullscreen=(self), geolocation=(), gyroscope=(), magnetometer=(), microphone=(), midi=(), payment=(), sync-xhr=()"
                        )
                    )
            )
            .authorizeHttpRequests(aut ->
                aut.requestMatchers( "/api/login").permitAll()
                    .requestMatchers("/api/authenticate").permitAll()
                    .requestMatchers("/api/enums/**").permitAll()
                    //.requestMatchers("/api/fiscalizations/check/device").permitAll()
                    .requestMatchers("/api/fiscalizations/sms/send").permitAll()
                    .requestMatchers("/api/admin/v1/leads/hamkor-smartpos").permitAll()
                    .requestMatchers("/api/admin/v1/leads/oddiy-smartpos").permitAll()
                    .requestMatchers("/api/admin/v1/leads/smartpos").permitAll()
                    .requestMatchers("/api/admin/v1/leads/shop-smartpos").permitAll()
                    .requestMatchers("/api/admin/v1/leads/nasiya-smartpos").permitAll()
                    .requestMatchers("/api/admin/v1/leads/business-smartpos").permitAll()
                    .requestMatchers("/api/cabinet/v1/leads/meta").permitAll()
                    .requestMatchers("/api/cabinet/v1/leads/telegram").permitAll()
                    .requestMatchers("/api/admin/v1/time-tracks/manual").hasAnyAuthority(Role.DIRECTOR.getRoleCode(), Role.HR.getRoleCode(), Role.SUPERVISOR.getRoleCode())
                    .requestMatchers("/api/admin/v1/time-tracks/{id}").hasAnyAuthority(Role.DIRECTOR.getRoleCode(), Role.HR.getRoleCode(), Role.SUPERVISOR.getRoleCode())
                    .requestMatchers("/api/admin/v1/uzcard-connection/create").hasAuthority(Role.UZCARD.getRoleCode())
                    .requestMatchers("/api/admin/v1/time-tracks/**").hasAnyAuthority(Role.INTEGRATION.getRoleCode(), Role.SUPER_ADMIN.getRoleCode())
                    .requestMatchers("/api/admin/billing-services/**").hasAnyAuthority(Role.getCompanyAndCabinetRoleCodes().toArray(new String[0]))
                    .requestMatchers("/api/admin/**").hasAnyAuthority(Role.getAdminRoles().toArray(new String[0]))
                    .requestMatchers("/api/cabinet/**").hasAnyAuthority(Role.getCabinetRoleCodes().toArray(new String[0]))
                    .requestMatchers("/api/public/**").permitAll()
                    .requestMatchers("/api/**").authenticated()
                    .requestMatchers("/api/admin/v1/leads/hamkor-smartpos").permitAll()
                    .requestMatchers("/api/admin/v1/leads/oddiy-smartpos").permitAll()
                    .requestMatchers("/api/admin/v1/leads/smartpos").permitAll()

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
    public AuthenticationProvider authenticationProvider(DomainUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
