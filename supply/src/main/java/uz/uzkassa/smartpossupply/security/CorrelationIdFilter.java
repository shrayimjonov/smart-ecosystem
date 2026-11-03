package uz.uzkassa.smartpossupply.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static uz.uzkassa.smartpossupply.constants.LoggingConstants.TRACE_ID_HEADER;
import static uz.uzkassa.smartpossupply.constants.LoggingConstants.TRACE_ID_MDC_KEY;


@Component
@RequiredArgsConstructor
public class CorrelationIdFilter extends OncePerRequestFilter {

    private static final List<String> EXCLUDED_PATHS = List.of(
            "/actuator/**",
            "/swagger-ui/**",
            "/v3/api-docs/**");

    private final PathMatcher pathMatcher;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String traceId = resolveTraceId(request.getHeader(TRACE_ID_HEADER));
        String previous = MDC.get(TRACE_ID_MDC_KEY);
        MDC.put(TRACE_ID_MDC_KEY, traceId);
        response.setHeader(TRACE_ID_HEADER, traceId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            if (previous != null) {
                MDC.put(TRACE_ID_MDC_KEY, previous);
            } else {
                MDC.remove(TRACE_ID_MDC_KEY);
            }
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestPath = request.getServletPath();
        return EXCLUDED_PATHS.stream()
                .anyMatch(pathToExclude -> pathMatcher.match(pathToExclude, requestPath));
    }

    private static String resolveTraceId(String incoming) {
        if (StringUtils.isNotBlank(incoming)) {
            return incoming;
        }
        return UUID.randomUUID().toString();
    }

}
