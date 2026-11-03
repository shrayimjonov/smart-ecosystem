package uz.uzkassa.smartpos.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

@Setter
@Getter
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {
    private String serverUrl;
    private String logChannelId;
    private String receiptChannelId;
    private String duplicateReceiptChannelId;
    private String clientBotToken;
    private String logBotToken;
    private String adminName;
    private String webSocketUrl;
    private String providerTin;
    private String trueApiUrl;
    private String tasnifApiUrl;
    private String jwtSecretKey;
    private long tokenValidityInSeconds;
    private Long tokenValidityInSecondsRememberMe;
    private Long refreshTokenValidityInHours;
    private final Client railways = new Client();
    private final Client cto = new Client();
    private final Client supply = new Client();
    private final Client soliq = new Client();
    private final Client factura = new Client();
    private final Client eImzo = new Client();
    private final Launcher launcher = new Launcher();
    private final Client billing = new Client();
    private final EtcSms etcSms = new EtcSms();
    private final Client ofd = new Client();
    private final Minio minio = new Minio();
    private final Apay apay = new Apay();
    private final MeningUyim meningUyim = new MeningUyim();

    @Getter
    @Setter
    public static class Minio {
        private String application;
        private String host;
        private String username;
        private String password;
    }

    @Getter
    @Setter
    public static class MeningUyim {
        private String host;
        private String username;
        private String password;
        private String secretKey;
    }

    @Getter
    @Setter
    public static class Client implements Serializable {
        private String host;
        private String username;
        private String password;
    }

    @Getter
    @Setter
    public static class Apay implements Serializable {
        private String host;
        private String username;
        private String password;
        private String deviceId;
        private String merchantId;
        private String checkServiceId;
        private String payServiceId;
    }

    @Setter
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Launcher {
        String host;
        String login;
        String password;
    }


    @Setter
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class EtcSms {
        String host;
        String cgpn;
        String username;
        String password;
        String smsForTelegram;
    }
}
