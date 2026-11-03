package uz.uzkassa.horeca.config;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
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
import uz.uzkassa.horeca.enums.Role;
import uz.uzkassa.horeca.security.CustomAccessDeniedHandler;
import uz.uzkassa.horeca.security.CustomAuthenticationEntryPoint;
import uz.uzkassa.horeca.security.DomainUserDetailsService;
import uz.uzkassa.horeca.security.jwt.JWTFilter;

import java.time.Duration;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SecurityConfiguration {

    CustomAuthenticationEntryPoint authenticationEntryPoint;
    CustomAccessDeniedHandler accessDeniedHandler;
    JWTFilter jwtFilter;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
                "/v3/api-docs/**", "/api-docs/**", "/swagger-ui/**",
                "/webjars/**", "/swagger-resources/**", "/swagger-ui.html",
                "/actuator/**");
    }

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
                                .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                                .referrerPolicy(referrer -> referrer.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                                .permissionsPolicy(permissions ->
                                        permissions.policy(
                                                "camera=(), fullscreen=(self), geolocation=(), gyroscope=(), magnetometer=(), microphone=(), midi=(), payment=(), sync-xhr=()"
                                        )
                                )
                )
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers("/api/public/**").permitAll()

                                .requestMatchers("/api/mobile/v1/account/login").permitAll()
                                .requestMatchers("/api/mobile/v1/account/check").permitAll()

                                .requestMatchers("/api/login/mobile").permitAll()
                                .requestMatchers("/api/v2/terminal/fiscalization/check").permitAll()
                                .requestMatchers("/api/account/reset-password/init").permitAll()
                                .requestMatchers("/api/account/reset-password/check").permitAll()
                                .requestMatchers("/api/account/reset-password/finish").permitAll()
                                .requestMatchers("/api/v1/terminals/users/roles").permitAll()

                                .requestMatchers("/api/admin/v1/account/login").permitAll()

                                .requestMatchers("/api/admin/**").hasAnyAuthority(Role.getAdminRoleCodes().toArray(new String[0]))
                                .requestMatchers("/api/cabinet/**").hasAnyAuthority(Role.getCabinetRoleCodes().toArray(new String[0]))

                                .requestMatchers("/api/**").authenticated()
                                .anyRequest()
                                .authenticated()
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
