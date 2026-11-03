package uz.uzkassa.cts.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {
    private String serverUrl;

    private final FileUploadProperty fileUploadProperty = new FileUploadProperty();
    private final SmsProperty smsProperty = new SmsProperty();
    private final EmailProperty emailProperty = new EmailProperty();
    private final KapitalBankProperty kapitalBankProperty = new KapitalBankProperty();
    private final BaseProperty soliqProperty = new BaseProperty();
    private final EtcSmsProperty etcSmsProperty = new EtcSmsProperty();
    private final BaseProperty facturaProperty = new BaseProperty();
    private final OfdProperty ofdProperty = new OfdProperty();
    private final TxkmProperty txkmProperty = new TxkmProperty();
    private final SmartposProperty smartposProperty = new SmartposProperty();
    private final LauncherProperty launcherProperty = new LauncherProperty();
    private final SshStorageProperty sshStorageProperty = new SshStorageProperty();
    private final TelegramBotProperty telegramBotProperty = new TelegramBotProperty();
    private final XfileProperty xfileProperty = new XfileProperty();
    private final BaseProperty kapitalBankLegalProperty = new BaseProperty();
    private final APayProperty apayProperty = new APayProperty();
    private final TelegramBotCaseProperty telegramBotCaseProperty = new TelegramBotCaseProperty();
    private final BaseProperty billingProperty = new BaseProperty();
    private final HorecaProperty horecaProperty = new HorecaProperty();
    private final SupplyProperty supplyProperty = new SupplyProperty();
    private final JWTProperty jwtProperty = new JWTProperty();
    private final BaseProperty ctsProperty = new BaseProperty();
    private final ApayGrpcProperty apayGrpcProperty = new ApayGrpcProperty();
    private final BaseProperty webSocketProperty = new BaseProperty();
    private final AiTimeTrackProperty aiTimeTrackProperty = new AiTimeTrackProperty();
    private final ErpMarketingProperty erpMarketingProperty = new ErpMarketingProperty();

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
    public static class BaseProperty extends Credential {

        String host;
        Long tokenExpiration;
    }

    @Setter
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class EmailProperty extends BaseProperty {
        private String from;
        private String checkEmail;
    }

    @Setter
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class SmsProperty extends BaseProperty {
        private String checkPhone;
    }

    @Setter
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class JWTProperty {
        String secretKey;
        Long tokenValidityInSeconds;
        Long tokenValidityInSecondsForRememberMe;
    }

    @Setter
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class HorecaProperty extends Credential {
        String authUrl;
        String companySyncUrl;
        String branchSyncUrl;
        String fiscalizationSyncUrl;
        Long tokenExpiration;
    }

    @Setter
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class SupplyProperty extends Credential {
        String authUrl;
        String companySyncUrl;
        String branchSyncUrl;
        String fiscalizationSyncUrl;
        Long tokenExpiration;
    }

    @Setter
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class FileUploadProperty {
        String uploadFolder;
        String downloadUrl;
    }

    @Setter
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class OfdProperty extends Credential {

        String authLoginUrl;
        String tokenUrl;
        String customerBalanceCheckUrl;
        String customerFiscalAddUrl;
        String customerFiscalListUrl;
        String verifyUrl;
        String customerBranchAddUrl;
        String customerBranchUpdateUrl;
        String customerBranchListUrl;
        String checkTinUrl;
        String checkPinflUrl;
        String generateApplicationFileUrl;
        String rejectApplicationUrl;
        String changeFmCcmUrl;
        String changeAddressUrl;
        String inactiveFiscalModuleListUrl;
        String neighborhoodListUrl;
        String scannerUrl;
        String activityGroupUrl;
        String activityListUrl;
        String categoryListUrl;
        String regionListUrl;
        String districtListUrl;
        Long tokenExpiration;
    }

    @Setter
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class TxkmProperty {
        String host;
        String n5Tin;
        String p100p10Tin;
    }

    @Setter
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class SmartposProperty extends Credential {
        String updateCustomerUrl;
        String companyByTinUrl;
        String companyByCtoId;
        String authenticateUrl;
        String smsSendUrl;
        String changeLogin;
        String changeAppType;
        String checkLoginExists;
        String changeOwner;
        String removeDeviceUrl;
        String syncCustomerBranchUrl;
        String bankTariffHistoryUrl;
        String attachManager;
        String customerSync;
        String removeDevice;
        String lastReceiptUrl;
        Long tokenExpiration;
    }

    @Setter
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class TelegramBotProperty {
        String channelId;
        String channelUrl;
    }

    @Setter
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class TelegramBotCaseProperty {
        String token;
        String channelId;
        String channelUrl;
        String groupId;
        String operatorUrl;
        String adminUrl;
        String deregisterkkmGroupId;
    }

    @Setter
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class LauncherProperty extends Credential {
        String authenticateUrl;
        String actionUrl;
        String updateTerminal;
    }

    @Setter
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class KapitalBankProperty extends Credential {
        String createTransaction;
        String pay;
        String check;
        String info;
        String cashId;
    }

    @Setter
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class SshStorageProperty {
        String remoteHost;
        String remoteDestination;
        String username;
        String password;
        String downloadUrl;
    }

    @Setter
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class XfileProperty extends Credential {
        String authenticateUrl;
        String invoiceUrl;
        String incomingInvoiceUrl;
        String invoiceByUuid;
        String primeTin;
        Long tokenExpiration;
    }

    @Setter
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class EtcSmsProperty extends BaseProperty {
        String authUserHost;
        String cgpn;
        String cgpn1;
    }

    @Setter
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class APayProperty extends Credential {
        String loginUrl;
        String companySyncUrl;
        String merchantSyncUrl;
        String categoryListUrl;
        String fiscalizationSyncUrl;
        String addUrl;
        String payUrl;
        String checkUrl;
        String kkmUrlCreate;
        String kkmUrlUpdate;
        String kkmUrlConfirm;
        String merchantId;
        String grpcLogin;
        String grpcCustomerBranch;
        String checkCompany;
        String quickRegister;
        String tariffList;
        String tariff;
        String getByBillId;
        String fiscalUrl;
        String checkServiceId;
        String payServiceId;
        String apayMerchantId;
        String silverTariffId;
        Long tokenExpiration;
        String deviceId;
    }

    @Setter
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ApayGrpcProperty extends Credential {
        String loginUrl;
        String customerBranch;
        Long tokenExpiration;
        String deviceId;
    }

    @Setter
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class AiTimeTrackProperty extends Credential {
        String authUrl;
        String employeeSyncUrl;
        Long tokenExpiration;
    }

    @Setter
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ErpMarketingProperty {
        String googleSheetSiteUrl;
    }
}
