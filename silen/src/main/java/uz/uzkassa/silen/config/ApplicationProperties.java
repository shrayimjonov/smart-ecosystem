package uz.uzkassa.silen.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

@Getter
@Setter
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {
    private final Client cto = new Client();
    private final Client xFile = new Client();
    private final Client billing = new Client();
    private final Client supplier = new Client();
    private final Client minio = new Client();
    private final Client eimzo = new Client();
    private final Client swagger = new Client();
    private final Client soliq = new Client();
    private final Client factura = new Client();
    private String pushUrl;
    private String serverUrl;
    private String logBotUrl;
    private String logBotToken;
    private String logChannelId;
    private String providerTin;
    private String jwtSecretKey;
    private long tokenValidityInSeconds;
    private long lineTokenValidityInSeconds;
    private String trueApiUrl;
    private String turonApiUrl;
    private String tasnifApiUrl;
    private String trueApiReport;
    private String clientSecret;
    private String lineUserId;
    private String lineOrganizationId;
    private String lineOrganizationTin;

    @Getter
    @Setter
    public static class Client implements Serializable {
        private String url;
        private String username;
        private String password;
        private String protocols;
        private String bucket;
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Getter
    @Setter
    public static class Credential {
        String username;
        String password;
    }
}
