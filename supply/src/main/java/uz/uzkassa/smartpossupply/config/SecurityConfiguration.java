package uz.uzkassa.smartpossupply.config;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import uz.uzkassa.smartpossupply.enums.Role;
import uz.uzkassa.smartpossupply.property.SwaggerSecurityProperties;
import uz.uzkassa.smartpossupply.security.CustomAccessDeniedHandler;
import uz.uzkassa.smartpossupply.security.CustomAuthenticationEntryPoint;
import uz.uzkassa.smartpossupply.security.DomainUserDetailsService;
import uz.uzkassa.smartpossupply.security.jwt.JWTFilter;
import uz.uzkassa.smartpossupply.security.CorrelationIdFilter;

import java.time.Duration;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class SecurityConfiguration {

    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final JWTFilter jwtFilter;
    private final CorrelationIdFilter correlationIdFilter;
    private final SwaggerSecurityProperties swaggerSecurityProperties;

    private static final String ADMIN_API_PATH_MATCHER = "/api/admin/**";
    private static final String PUBLIC_API_PATH_MATCHER = "/api/public/**";
    private static final String PROTECTED_API_PATH_MATCHER = "/api/**";
    private static final String ACTUATOR_API_PATH_MATCHER = "/actuator/**";
    private static final String MANAGEMENT_API_PATH_MATCHER = "/management/**";
    private static final String MARKETPLACE_API_PATH_MATCHER = "/api/marketplace/**";
    private static final String MOBILE_API_PATH_MATCHER = "/api/mobile/**";
    private static final String CABINET_API_PATH_MATCHER = "/api/cabinet/**";
    private static final String HIPPO_API_PATH_MATCHER = "/api/hippo/**";
    private static final String TELEGRAM_API_PATH_MATCHER = "/api/telegram/**";

    private static final String[] SWAGGER_MATCHERS = {
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**"
    };

    private static final String[] BILLING_API_MATCHERS = {
            "/api/cabinet/v1/billing/**",
            "/api/notification/billing/**",
            "/api/billing-permission/**"
    };

    @Bean
    @Order(1)
    public SecurityFilterChain swaggerFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(SWAGGER_MATCHERS)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Order(2)
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
                                .requestMatchers(SWAGGER_MATCHERS).denyAll()
                                .requestMatchers("/api/authenticate").permitAll()
                                .requestMatchers("/api/register").permitAll()
                                .requestMatchers("/api/activate").permitAll()
                                .requestMatchers(PUBLIC_API_PATH_MATCHER).permitAll()
                                .requestMatchers(ACTUATOR_API_PATH_MATCHER).permitAll()
                                .requestMatchers(MANAGEMENT_API_PATH_MATCHER).permitAll()
                                .requestMatchers("/api/cabinet/v1/invoices/webhook/**").permitAll()
                                .requestMatchers("/api/cabinet/v1/waybills/webhook/**").permitAll()
                                .requestMatchers("/api/cabinet/v1/account/timestamp", "/api/cabinet/v1/account/timestamp-signature").permitAll()
                                .requestMatchers("/api/admin/v1/account/login", "/api/admin/v1/account/logout").permitAll()
                                .requestMatchers("/api/cabinet/v1/marketplace-products/sync").permitAll()
                                .requestMatchers(TELEGRAM_API_PATH_MATCHER).permitAll()
                                .requestMatchers("/api/marketplace/v1/account/login").permitAll()
                                .requestMatchers(
                                        "/api/cabinet/v1/account/login",
                                        "/api/mobile/v1/account/login",
                                        "/api/mobile/v1/account/refresh-token",
                                        "/api/mobile/v1/system-upgrades/available-versions",

                                        "/api/cabinet/v1/account/logout",
                                        "/api/mobile/v1/account/logout",

                                        "/api/cabinet/v1/account/register",
                                        "/api/admin/v1/account/register",

                                        "/api/cabinet/v1/account/activate",
                                        "/api/cabinet/v1/account/activate/finish",
                                        "/api/cabinet/v1/account/send-activation-key",
                                        "/api/cabinet/v1/account/resend-activation-key",
                                        "/api/admin/v1/account/activate",
                                        "/api/admin/v1/account/activate/finish",
                                        "/api/admin/v1/account/send-activation-key",
                                        "/api/admin/v1/account/resend-activation-key"
                                ).permitAll()
                                .requestMatchers(ADMIN_API_PATH_MATCHER).hasAnyAuthority(Role.getAdminRoleCodes().toArray(new String[]{}))
                                .requestMatchers(BILLING_API_MATCHERS).hasAnyAuthority(Role.getBillingRoleCodes().toArray(new String[]{}))
                                .requestMatchers(CABINET_API_PATH_MATCHER).hasAnyAuthority(Role.getCabinetRoleCodes().toArray(new String[]{}))
                                .requestMatchers(MOBILE_API_PATH_MATCHER).hasAnyAuthority(Role.getCabinetRoleCodes().toArray(new String[]{}))
                                .requestMatchers(HIPPO_API_PATH_MATCHER).hasAnyAuthority(Role.ROLE_HIPPO.getCode())
                                //.requestMatchers(MARKETPLACE_API_PATH_MATCHER).hasAnyAuthority(Role.ROLE_MARKETPLACE.getCode())
                                .requestMatchers(MARKETPLACE_API_PATH_MATCHER).permitAll()
                                .requestMatchers("/api/marketplace/v1/account/refresh-token").permitAll()
                                .requestMatchers("/api/admin/v1/system-upgrades/**").hasAnyAuthority(Role.ROLE_SUPER_ADMIN.getCode())
                                .requestMatchers("/api/integration/v1/1C/**").hasAnyAuthority(Role.ROLE_1C_INTEGRATION.getCode(), Role.ROLE_SUPER_ADMIN.getCode())
                                .requestMatchers(PROTECTED_API_PATH_MATCHER)
                                .authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions ->
                        exceptions
                                .authenticationEntryPoint(authenticationEntryPoint)
                                .accessDeniedHandler(accessDeniedHandler)
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(correlationIdFilter, JWTFilter.class);
        return http.build();
    }

    @Bean
    public FilterRegistrationBean<JWTFilter> jwtFilterRegistration(JWTFilter filter) {
        FilterRegistrationBean<JWTFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<CorrelationIdFilter> correlationIdFilterRegistration(CorrelationIdFilter filter) {
        FilterRegistrationBean<CorrelationIdFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public UserDetailsService swaggerUserDetailsService() {
        UserDetails user = User.builder()
                .username(swaggerSecurityProperties.username())
                .password(passwordEncoder().encode(swaggerSecurityProperties.password()))
                .roles(Role.ROLE_SWAGGER.name().split("_")[1])
                .build();

        return new InMemoryUserDetailsManager(user);
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
