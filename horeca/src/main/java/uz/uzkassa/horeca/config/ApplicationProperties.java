package uz.uzkassa.horeca.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.util.UriComponentsBuilder;

@Getter
@Setter
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private final CtsConfig ctsConfig = new CtsConfig();
    private final TasnifConfig tasnifConfig = new TasnifConfig();
    private final SmsConfig smsConfig = new SmsConfig();
    private final SoliqConfig soliqConfig = new SoliqConfig();
    private final EimzoClient eimzoConfig = new EimzoClient();
    private final AmazonConfig amazonConfig = new AmazonConfig();
    private final FireBaseConfig fireBaseConfig = new FireBaseConfig();
    private final SmartposConfig smartposConfig = new SmartposConfig();
    private final TelegramBotConfig telegramBotConfig = new TelegramBotConfig();
    private final BaseConfig billing = new BaseConfig();
    private final BaseConfig facturaConfig = new BaseConfig();
    private final ApayConfig apayConfig = new ApayConfig();
    private String serverUrl;
    private String webSocketUrl;

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Getter
    @Setter
    public static class Credential {

        String username;
        String password;
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Getter
    @Setter
    public static class BaseConfig extends Credential {

        String host;

        public UriComponentsBuilder baseUri() {
            return UriComponentsBuilder.fromUriString(host);
        }
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Getter
    @Setter
    public static class EimzoClient {

        String host;
        String frontendTimestampPath;
        String backendVerifyPath;
        String xRealIP;
        String internalHost;
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Getter
    @Setter
    public static class TasnifConfig extends BaseConfig {

        String basketProductsPath;
        String mxikHistoryPath;
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Getter
    @Setter
    public static class SoliqConfig extends BaseConfig {

        String companyDetailPath;
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Getter
    @Setter
    public static class CtsConfig extends BaseConfig {

        String authPath;
        String companyByTinPath;
        String companyByTinSoliqPath;
        String fiscalCheckPath;
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Getter
    @Setter
    public static class SmartposConfig extends BaseConfig {

        String authPath;
        String createReceiptPath;
        String receiptListPath;
        String receiptGetPath;
        String receiptStatisticPath;
        String receiptBatchSendPath;
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Getter
    @Setter
    public static class FacturaConfig extends BaseConfig {

        String authPath;
        String createReceiptPath;
        String receiptListPath;
        String receiptGetPath;
        String receiptStatisticPath;
        String receiptBatchSendPath;
    }

    @Setter
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class AmazonConfig {

        String publicBucketName;
        String privateBucketName;
        String accessKey;
        String secretKey;
    }

    @Setter
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class FireBaseConfig {
        String serviceAccount;
    }

    @Setter
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class SmsConfig extends BaseConfig {

        String cgpn;
    }

    @Setter
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class TelegramBotConfig {

        String channelId;
        String channelUrl;
        String token;
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Getter
    @Setter
    public static class ApayConfig extends BaseConfig {

        String authPath;
        String epsServicesPath;
        String callBackUrl;
        String deviceId;
    }
}
