package uz.uzkassa.smartpos.security;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;
import uz.uzkassa.smartpos.enums.HeaderKeys;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HeaderUtils {

    public static String getCurrentHeaderByKey(HeaderKeys key) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            String value = (((ServletRequestAttributes) requestAttributes).getRequest()).getHeader(key.name());
            if (StringUtils.isNotBlank(value)) {
                try {
                    return value;
                } catch (Exception ignored) {
                }
            }
        }
        return null;
    }

    public static Map<String, String> extractHeaders(final ContentCachingRequestWrapper req) {
        final Map<String, String> map = new HashMap<>();
        Collections.list(req.getHeaderNames())
            .forEach(x -> map.put(x, req.getHeader(x)));
        return map;
    }

    public static String getRealIP() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            String value = (((ServletRequestAttributes) requestAttributes).getRequest()).getHeader("X-Real-IP");
            if (StringUtils.isNotBlank(value)) {
                try {
                    return value;
                } catch (Exception ignored) {
                }
            }
        }
        return null;
    }

}
