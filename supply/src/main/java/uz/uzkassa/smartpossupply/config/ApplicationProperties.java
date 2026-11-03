package uz.uzkassa.smartpossupply.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
@Getter
@Setter
public class ApplicationProperties {

    private String serverUrl;
    private String domain;
    private final ErpConfig erpConfig = new ErpConfig();
    private final MarketplaceConfig marketplaceConfig = new MarketplaceConfig();
    private final BaseConfig tasnifConfig = new BaseConfig();
    private final SmartposConfig smartposConfig = new SmartposConfig();
    private final SmsConfig smsConfig = new SmsConfig();
    private final MinioStorageConfig minioStorageConfig = new MinioStorageConfig();
    private final OfdConfig ofdConfig = new OfdConfig();
    private final BaseConfig soliqConfig = new BaseConfig();
    private final FacturaConfig facturaConfig = new FacturaConfig();
    private final BaseConfig silenConfig = new BaseConfig();
    private final BaseConfig turonConfig = new BaseConfig();
    private final BaseConfig launcher = new BaseConfig();
    private final AslBelgiConfig aslBelgiConfig = new AslBelgiConfig();
    private final TelegramBotApplicationLogConfig telegramBotApplicationLogConfig = new TelegramBotApplicationLogConfig();
    private final TelegramBotFacturaConfig telegramBotFacturaConfig = new TelegramBotFacturaConfig();
    private final TelegramConnectTariffConfig telegramConnectTariffConfig = new TelegramConnectTariffConfig();
    private final BaseConfig billing = new BaseConfig();
    private final XtraceConfig xtraceConfig = new XtraceConfig();
    private final YandexAddressConfig yandexAddressConfig = new YandexAddressConfig();
    private final BaseConfig eimzoConfig = new BaseConfig();
    private final BaseConfig pushkassaConfig = new BaseConfig();
    private final OtpConfig otpConfig = new OtpConfig();
    private final ReportEngineConfig reportEngine = new ReportEngineConfig();

    @FieldDefaults(level = AccessLevel.PROTECTED)
    @Getter
    @Setter
    public static class Credential {

        String username;
        String password;
    }

    @FieldDefaults(level = AccessLevel.PROTECTED)
    @Getter
    @Setter
    public static class BaseConfig extends Credential {

        String host;
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Getter
    @Setter
    public static class XtraceConfig {

        String checkApiKeyUrl;
        String refreshApiKeyUrl;
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Getter
    @Setter
    public static class MarketplaceConfig extends Credential {

        String authUrl;
        String companyBatchSyncUrl;
        String companySyncUrl;
        String companyUpdateUrl;
        String companyBankSyncUrl;
        String branchSyncUrl;
        String applicationSyncUrl;
        String applicationRemoveUrl;
        String applicationReturnUrl;
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Getter
    @Setter
    public static class ErpConfig extends Credential {

        String authUrl;
        String customerSyncUrl;
        String customerBranchSyncUrl;
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Getter
    @Setter
    public static class YandexAddressConfig {
        String url;
        String apiKey;
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Getter
    @Setter
    public static class AslBelgiConfig {

        String url;
        String url2;
        String facade;
        String privateOffice;
        String baseXtraceUrl;
        String publicCheckInfoUrl;
        String privateCheckInfoUrl;
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Getter
    @Setter
    public static class FacturaConfig extends Credential {

        String url;
        String url0;
        String url1;
        String url2;
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Getter
    @Setter
    public static class SmartposConfig extends Credential {

        String authUrl;
        String categoryListUrl;
        String productListUrl;
        String orderUrl;
        String acceptanceTransferActUrl;
        String adminBaseUrl;
    }

    @Setter
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class SmsConfig extends BaseConfig {

        String authUserHost;
        String cgpn;
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Getter
    @Setter
    public static class MinioStorageConfig extends BaseConfig {

        String application;

        public String getWithBaseUrl(String path) {
            return String.format("%s/%s", this.host, path);
        }
    }

    @Setter
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class OfdConfig extends Credential {

        String authLoginUrl;
        String token;
        String checkTinUrl;
        String checkPinflUrl;
        String tinUzkassa;
        String productSearchUrl;
    }

    @Setter
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class TelegramBotBaseConfig {
        String token;
        String proxyUrl;
    }

    @Setter
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class TelegramBotApplicationLogConfig extends TelegramBotBaseConfig {

        String channelId;
        String channelUrl;
    }

    @Setter
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class TelegramBotFacturaConfig extends TelegramBotBaseConfig {

        String channelId;
        String channelUrl;
    }


    @Setter
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class TelegramConnectTariffConfig extends TelegramBotBaseConfig {
        String channelId;
        String channelUrl;
    }

    @Setter
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class OtpConfig {
        String secretKey;
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Getter
    @Setter
    public static class ReportEngineConfig {
        long maxCells = 50_000;
        long maxRows = 100_000;
        long maxPeriodDays = 731;
        int statementTimeoutMs = 30_000;
        int maxConcurrentPerCompany = 2;
    }
}
