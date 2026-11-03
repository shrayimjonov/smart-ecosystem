package uz.uzkassa.smartpossupply.constants;

public interface ApiConstants {
    String api = "/api";

    String admin = "/admin";
    String cabinet = "/cabinet";
    String mobile = "/mobile";
    String terminal = "/terminal";
    String publicStr = "/public";
    String integration = "/integration";
    String hippo = "/hippo";
    String marketplace = "/marketplace";
    String telegram = "/telegram";

    String version = "/v1";

    String adminRootApi = api + admin + version;
    String cabinetRootApi = api + cabinet + version;
    String mobileRootApi = api + mobile + version;
    String terminalRootApi = api + terminal + version;
    String publicRootApi = api + publicStr + version;
    String integrationRootApi = api + integration + version;
    String hippoRootApi = api + hippo + version;
    String marketplaceRootApi = api + marketplace + version;
    String telegramRootApi = api + telegram + version;
    String swaggerRootApi = api;

    String dashboard = "/dashboard";
    String transfer = "/transfer";
    String authenticateApi = "/authenticate";
    String productApi = "/products";
    String activityTypeApi = "/activity-types";
    String aggregationApi = "/aggregations";
    String partyApi = "/parties";
    String partyProductApi = "/party-products";
    String aggregationBlockApi = "/aggregation-blocks";
    String aggregationHistoryApi = "/aggregation-histories";
    String aslBelgiApi = "/asl-belgi";
    String markApi = "/marks";
    String bankApi = "/banks";
    String apayApi = "/apay";
    String unitApi = "/units";
    String fileUploadApi = "/file-uploads";
    String accountApi = "/account";
    String aicApi = "/aic";
    String securityApi = "/securities";
    String countryApi = "/countries";
    String regionApi = "/regions";
    String districtApi = "/districts";
    String applicationApi = "/applications";
    String commentApi = "/comments";
    String yandexApi = "/yandex";
    String branchApi = "/branches";
    String agentApi = "/agents";
    String checklistApi = "/checklists";
    String checklistConditionApi = "/checklist-conditions";
    String checklistQuestionnaireApi = "/checklist-questionnaires";
    String mailingListApi = "/mailing-list";
    String mailingListItemApi = "/mailing-list-items";
    String scheduleMessageApi = "/schedule-messages";
    String genericSettingsApi = "/generic-settings";
    String companySettingsApi = "/company-settings";
    String settingsGroupApi = "/settings-groups";
    String xtraceTokenApi = "/xtrace-tokens";

    String companyDeliverySettingsApi = "/company-delivery-settings";
    String companyPaymentSettingsApi = "/company-payment-settings";
    String marketplaceSettingsApi = "/marketplace-settings";

    String customerBranch = "/customer-branches";
    String customerBranchProduct = "/customer-branch-products";
    String contractApi = "/contracts";
    String categoryApi = "/categories";
    String customerGroupApi = "/customer-groups";
    String categorySortApi = "/category-sort";
    String productSortApi = "/product-sort";
    String companyApi = "/companies";
    String marketplaceApplicationApi = "/marketplace-applications";
    String committentApi = "/committents";
    String companyBankApi = "/company-banks";
    String companyFieldHistoryApi = "/company-field-histories";
    String companyAggregationInfoApi = "/aggregation-info";
    String companyApayInfoApi = "/apay-info";
    String employeeApi = "/employees";
    String expeditorApi = "/expeditors";
    String userApi = "/users";
    String userCompanyApi = "/user-companies";
    String stockInApi = "/stock-in";
    String stockInProductApi = "/stock-in-products";
    String stockTransferApi = "/stock-transfers";
    String stockAdjustmentApi = "/stock-adjustments";
    String stockOutApi = "/stock-out";
    String stockReportApi = "/stock-reports";
    String invoiceApi = "/invoices";
    String orderApi = "/orders";
    String offlineOrderApi = "/offline-orders";
    String order1CApi = "/orders/1c";
    String stockIn1CApi = "/stock-in/1c";
    String markOrderApi = "/mark-orders";
    String aggregationOrderApi = "/aggregation-orders";
    String markOrderPartyApi = "/mark-orders-parties";
    String supplierApi = "/suppliers";
    String customerApi = "/customers";
    String warehouseApi = "/warehouses";
    String warehouseProductApi = "/warehouse-products";
    String warehouseExpeditorApi = "/warehouse-expeditors";
    String receiptApi = "/receipts";
    String facturaApi = "/factura";
    String facturaHostApi = "/factura-hosts";
    String lgotaApi = "/lgota";
    String lgotaPrefixApi = "/lgota-prefixes";
    String lotApi = "/lots";
    String reportApi = "/reports";
    String materialReportApi = "/material-reports";
    String permissionApi = "/permissions";
    String cacheApi = "/caches";
    String billingApi = "/billing";
    String equipmentApi = "/equipments";
    String acceptanceTransferActApi = "/acceptance-transfer-acts";
    String telegramIntegrationApi = "/telegram-integrations";
    String shiftApi = "/shift";
    String shiftReportApi = "/shift-reports";
    String planRouteApi = "/plan-routes";
    String planRouteCustomerBranchApi = "/plan-route-customer-branches";
    String planRouteDashboardApi = "/plan-route-dashboard";
    String planRouteTemplateApi = "/plan-route-templates";
    String agentTrackingApi = "/agent-tracking";
    String notificationTemplateApi = "/notification-templates";
    String applicationTypeApi = "/application-types";
    String notificationApi = "/notifications";
    String transactionApi = "/transactions";
    String priceListApi = "/price-list";
    String exportReportApi = "/export-reports";
    String importReportApi = "/import-reports";
    String productContractApi = "/product-contracts";
    String postApi = "/post";
    String waybillApi = "/waybills";
    String systemUpgradeApi = "/system-upgrades";
    String terminalApi = "/terminals";
    String terminalLogApi = "/terminal-logs";
    String launcherApi = "/launcher";
    String discountApi = "/discounts";
    String stockOptimizationApi = "/stock-optimization";
    String branchBankApi = "/branch-banks";
    String vehicleApi = "/vehicles";
    String containerApi = "/containers";
    String logApi = "/logs";
    String tariffApi = "/tariffs";
    String tripApi = "/trips";

    String id = "/{id}";
    String lookUp = "/lookup";
    String offline = "/offline";
    String items = "/items";
    String reverse = "/reverse/{id}";
    String sync = "/sync";
    String resync = "/resync";
    String batch = "/batch";
    String batchSync = "/batch-sync";
    String all = "/all";
    String types = "/types";
    String groups = "/groups";
    String packageTypes = "/package-types";
    String statuses = "/statuses";
    String fieldTypes = "/field-types";
    String reasons = "/reasons";
    String login = "/login";
    String logout = "/logout";
    String check = "/check";
    String register = "/register";
    String current = "/current";
    String profile = "/profile";
    String exportCsv = "/export-csv";
    String exportExcel = "/export-excel";
    String importStr = "/import";
    String businessTypes = "/business-types";
    String documentTypes = "/document-types";
    String vats = "/vats";
    String filters = "/filters";

    String sendActivationKey = "/send-activation-key";
    String reSendActivationKey = "/resend-activation-key";

    String changePasswordInit = "/change-password/init";
    String changePasswordCheck = "/change-password/check";
    String changePasswordFinish = "/change-password/finish";

    String changeUsernameInit = "/change-username/init";
    String changeUsernameCheck = "/change-username/check";
    String changeUsernameFinish = "/change-username/finish";

    String forgotPasswordInit = "/forgot-password/init";
    String forgotPasswordCheck = "/forgot-password/check";
    String forgotPasswordFinish = "/forgot-password/finish";

    String refreshToken = "/refresh-token";

    String activateFinish = "/activate/finish";
    String roles = "/roles";
    String activate = "/activate";
    String validate = "/validate";
    String send = "/send";
    String cancel = "/cancel";
    String cancelDraft = "/cancel-draft";
    String accept = "/accept";
    String reject = "/reject";
    String balanceReport = "/balance-report";
    String report = "/report";
    String statistics = "/statistics";
    String updateStatus = "/{id}/status";
    String withdraw = "/withdraw/{id}";
    String copy = "/{id}/copy";
    String paymentTypes = "/payment-types";
    String updateType = "/{id}/type";
    String updateNumber = "/{id}/number";
    String updateDeliveryDate = "/{id}/delivery-date";
    String upload = "/upload";
    String download = "/download";
    String stamps = "/stamps";
    String days = "/days";
    String pdf = "/pdf";
    String stockReturnApi = "/stock-returns";
    String cashFlowApi = "/cash-flows";
    String financialOperationApi = "/financial-operation";
    String productSerialApi = "/product-serials";
    String clickhouse = "/clickhouse";
    String logHistory = "/log-histories";

    String invoiceStatistics = "/invoice-statistics";
    String topClients = "/top-clients";
    String productsResidue = "/products-residue";
    String topProducts = "/top-products";
    String companyRevenue = "/company-revenue";
    String reportDatasets = "/datasets";
    String reportPreview = "/preview";
    String oneC = "/1C";
    String apay = "/apay";

    String invoice = "/invoice";
    String services = "/services";
    String quote = "/quote";

    String webhookReceived = "/webhook/received";
    String webhookAccepted = "/webhook/accepted";
    String webhookAgentAccepted = "/webhook/agent-accepted";
    String webhookRejected = "/webhook/rejected";
    String webhookCancelled = "/webhook/cancelled";
    String webhookUpdated = "/webhook/updated";

    String competitorProducts = "/competitor-products";
    String KPI = "/kpis";
    String reconciliationAct = "/reconciliation-act";
    String supervisor = "/supervisors";

    String cabinetSupervisorRootApi = cabinetRootApi + supervisor;

    String mobileSupervisorRootApi = mobileRootApi + supervisor;

    String cabinetTripRootApi = cabinetRootApi + tripApi;

    String adminProductRootApi = adminRootApi + productApi;
    String cabinetProductRootApi = cabinetRootApi + productApi;
    String marketplaceProductRootApi = marketplaceRootApi + productApi;
    String mobileProductRootApi = mobileRootApi + productApi;
    String publicProductRootApi = publicRootApi + productApi;

    String adminBranchRootApi = adminRootApi + branchApi;
    String cabinetBranchRootApi = cabinetRootApi + branchApi;
    String mobileBranchRootApi = mobileRootApi + branchApi;

    String cabinetAgentRootApi = cabinetRootApi + agentApi;

    String cabinetChecklistRootApi = cabinetRootApi + checklistApi;
    String mobileChecklistRootApi = mobileRootApi + checklistApi;

    String cabinetChecklistConditionRootApi = cabinetRootApi + checklistConditionApi;

    String cabinetChecklistQuestionnaireRootApi = cabinetRootApi + checklistQuestionnaireApi;
    String mobileChecklistQuestionnaireRootApi = mobileRootApi + checklistQuestionnaireApi;

    String cabinetXtraceTokenRootApi = cabinetRootApi + xtraceTokenApi;

    String cabinetMailingListRootApi = cabinetRootApi + mailingListApi;
    String cabinetMailingListItemRootApi = cabinetRootApi + mailingListItemApi;
    String cabinetScheduleMessageRootApi = cabinetRootApi + scheduleMessageApi;

    String adminMailingListRootApi = adminRootApi + mailingListApi;
    String adminMailingListItemRootApi = adminRootApi + mailingListItemApi;
    String adminScheduleMessageRootApi = adminRootApi + scheduleMessageApi;

    String cabinetCustomerBranchRootApi = cabinetRootApi + customerBranch;
    String adminCustomerBranchRootApi = adminRootApi + customerBranch;
    String mobileCustomerBranchRootApi = mobileRootApi + customerBranch;
    String terminalCustomerBranchRootApi = terminalRootApi + customerBranch;
    String publicCustomerBranchRootApi = publicRootApi + customerBranch;

    String oneCIntegrationRootApi = integrationRootApi + oneC;

    String cabinetContractRootApi = cabinetRootApi + contractApi;
    String adminContractRootApi = adminRootApi + contractApi;
    String mobileContractRootApi = mobileRootApi + contractApi;
    String terminalContractRootApi = terminalRootApi + contractApi;

    String adminCategoryRootApi = adminRootApi + categoryApi;
    String cabinetCategoryRootApi = cabinetRootApi + categoryApi;
    String mobileCategoryRootApi = mobileRootApi + categoryApi;
    String publicCategoryRootApi = publicRootApi + productApi;
    String marketplaceCategoryRootApi = marketplaceRootApi + categoryApi;

    String cabinetCustomerGroupRootApi = cabinetRootApi + customerGroupApi;

    String cabinetFacturaRootApi = cabinetRootApi + facturaApi;
    String adminFacturaRootApi = adminRootApi + facturaApi;

    String adminFacturaHostRootApi = adminRootApi + facturaHostApi;

    String cabinetLgotaRootApi = cabinetRootApi + lgotaApi;

    String cabinetLgotaPrefixRootApi = cabinetRootApi + lgotaPrefixApi;
    String adminLgotaPrefixRootApi = adminRootApi + lgotaPrefixApi;

    String cabinetLotRootApi = cabinetRootApi + lotApi;

    String adminTelegramIntegrationRootApi = adminRootApi + telegramIntegrationApi;

    String telegramCategoryRootApi = telegramRootApi + categoryApi;
    String telegramOrderRootApi = telegramRootApi + orderApi;
    String telegramCompanyRootApi = telegramRootApi + companyApi;
    String telegramCustomerRootApi = telegramRootApi + customerApi;
    String telegramProductRootApi = telegramRootApi + productApi;
    String telegramConnectTariffRootApi = cabinetRootApi + tariffApi;

    String adminSupplierRootApi = adminRootApi + supplierApi;
    String cabinetSupplierRootApi = cabinetRootApi + supplierApi;
    String mobileSupplierRootApi = mobileRootApi + supplierApi;

    String adminCustomerRootApi = adminRootApi + customerApi;
    String cabinetCustomerRootApi = cabinetRootApi + customerApi;
    String mobileCustomerRootApi = mobileRootApi + customerApi;
    String terminalCustomerRootApi = terminalRootApi + customerApi;

    String cabinetOrderRootApi = cabinetRootApi + orderApi;
    String cabinetOrder1CRootApi = cabinetRootApi + order1CApi;

    String hippoOrderRootApi = hippoRootApi + orderApi;

    String adminOrderRootApi = adminRootApi + orderApi;
    String mobileOrderRootApi = mobileRootApi + orderApi;
    String mobileOfflineOrderRootApi = mobileRootApi + offlineOrderApi;
    String terminalOrderRootApi = terminalRootApi + orderApi;
    String publicOrderRootApi = publicRootApi + orderApi;

    String cabinetMarkOrderRootApi = cabinetRootApi + markOrderApi;
    String cabinetMarkOrderPartyRootApi = cabinetRootApi + markOrderPartyApi;

    String cabinetAggregationOrderRootApi = cabinetRootApi + aggregationOrderApi;

    String cabinetWarehouseRootApi = cabinetRootApi + warehouseApi;
    String adminWarehouseRootApi = adminRootApi + warehouseApi;
    String mobileWarehouseRootApi = mobileRootApi + warehouseApi;

    String cabinetWarehouseProductRootApi = cabinetRootApi + warehouseProductApi;
    String mobileWarehouseProductRootApi = mobileRootApi + warehouseProductApi;
    String terminalWarehouseProductRootApi = terminalRootApi + warehouseProductApi;
    String adminWarehouseProductRootApi = adminRootApi + warehouseProductApi;

    String cabinetWarehouseExpeditorRootApi = cabinetRootApi + warehouseExpeditorApi;

    String cabinetMarketplaceApplicationRootApi = cabinetRootApi + marketplaceApplicationApi;
    String adminMarketplaceApplicationRootApi = adminRootApi + marketplaceApplicationApi;

    String adminCompanyRootApi = adminRootApi + companyApi;
    String cabinetCompanyRootApi = cabinetRootApi + companyApi;
    String marketplaceCompanyRootApi = marketplaceRootApi + companyApi;
    String marketplaceCompanyBankRootApi = marketplaceRootApi + companyBankApi;

    String cabinetCompanyBankRootApi = cabinetRootApi + companyBankApi;
    String adminCompanyBankRootApi = adminRootApi + companyBankApi;

    String cabinetCompanyFieldHistoryRootApi = cabinetRootApi + companyFieldHistoryApi;

    String cabinetReportRootApi = cabinetRootApi + reportApi;

    String cabinetMaterialReportRootApi = cabinetRootApi + materialReportApi;

    String adminEmployeeRootApi = adminRootApi + employeeApi;
    String cabinetEmployeeRootApi = cabinetRootApi + employeeApi;
    String mobileEmployeeRootApi = mobileRootApi + employeeApi;
    String marketplaceEmployeeRootApi = marketplaceRootApi + employeeApi;

    String cabinetExpeditorRootApi = cabinetRootApi + expeditorApi;

    String adminUserRootApi = adminRootApi + userApi;

    String adminUserCompanyRootApi = adminRootApi + userCompanyApi;
    String cabinetUserCompanyRootApi = cabinetRootApi + userCompanyApi;

    String adminStockInRootApi = adminRootApi + stockInApi;
    String cabinetStockInRootApi = cabinetRootApi + stockInApi;
    String mobileStockInRootApi = mobileRootApi + stockInApi;
    String cabinetStockIn1CRootApi = cabinetRootApi + stockIn1CApi;

    String cabinetStockInProductRootApi = cabinetRootApi + stockInProductApi;

    String cabinetStockTransferRootApi = cabinetRootApi + stockTransferApi;
    String terminalStockTransferRootApi = terminalRootApi + stockTransferApi;

    String cabinetStockAdjustmentRootApi = cabinetRootApi + stockAdjustmentApi;

    String adminStockOutRootApi = adminRootApi + stockOutApi;
    String cabinetStockOutRootApi = cabinetRootApi + stockOutApi;
    String cabinetStockReportRotApi = cabinetRootApi + stockReportApi;
    String mobileStockOutRootApi = mobileRootApi + stockOutApi;
    String terminalStockOutRootApi = terminalRootApi + stockOutApi;

    String cabinetInvoiceRootApi = cabinetRootApi + invoiceApi;

    String publicActivityTypeRootApi = publicRootApi + activityTypeApi;

    String cabinetAggregationRootApi = cabinetRootApi + aggregationApi;
    String cabinetPartyRootApi = cabinetRootApi + partyApi;
    String cabinetPartyProductRootApi = cabinetRootApi + partyProductApi;

    String cabinetAggregationHistoryRootApi = cabinetRootApi + aggregationHistoryApi;
    String cabinetAggregationBlockRootApi = cabinetRootApi + aggregationBlockApi;

    String cabinetMarkRootApi = cabinetRootApi + markApi;

    String adminBankRootApi = adminRootApi + bankApi;
    String cabinetBankRootApi = cabinetRootApi + bankApi;

    String publicUnitRootApi = publicRootApi + unitApi;

    String adminAccountRootApi = adminRootApi + accountApi;
    String cabinetAccountRootApi = cabinetRootApi + accountApi;

    String publicAccountRootApi = publicRootApi + accountApi;
    String mobileAccountRootApi = mobileRootApi + accountApi;
    String terminalAccountRootApi = terminalRootApi + accountApi;
    String marketplaceAccountRootApi = marketplaceRootApi + accountApi;

    String cabinetAicRootApi = cabinetRootApi + aicApi;
    String mobileAicRootApi = mobileRootApi + aicApi;

    String adminSecurityRootApi = adminRootApi + securityApi;

    String publicRegionRootApi = publicRootApi + regionApi;
    String publicDistrictRootApi = publicRootApi + districtApi;
    String publicCountryRootApi = publicRootApi + countryApi;

    String publicApplicationRootApi = publicRootApi + applicationApi;
    String adminApplicationRootApi = adminRootApi + applicationApi;

    String adminCommentRootApi = adminRootApi + commentApi;
    String cabinetCommentRootApi = cabinetRootApi + commentApi;

    String mobileYandexRootApi = mobileRootApi + yandexApi;

    String adminPermissionRootApi = adminRootApi + permissionApi;
    String cabinetPermissionRootApi = cabinetRootApi + permissionApi;

    String cabinetAslBelgiRootApi = cabinetRootApi + aslBelgiApi;

    String cabinetCompanyAggregationInfoApi = cabinetCompanyRootApi + companyAggregationInfoApi;
    String adminCompanyAggregationInfoApi = adminCompanyRootApi + companyAggregationInfoApi;

    String cabinetAcceptanceTransferActRootApi = cabinetRootApi + acceptanceTransferActApi;
    String mobileAcceptanceTransferActRootApi = mobileRootApi + acceptanceTransferActApi;
    String adminAcceptanceTransferActRootApi = adminRootApi + acceptanceTransferActApi;

    String adminCacheRootApi = adminRootApi + cacheApi;

    String cabinetReceiptRootApi = cabinetRootApi + receiptApi;
    String terminalReceiptRootApi = terminalRootApi + receiptApi;

    String shiftRootApi = cabinetRootApi + shiftApi;
    String shiftReportRootApi = cabinetRootApi + shiftReportApi;

    String cabinetApayRootApi = cabinetRootApi + apayApi;

    String cabinetBillingInvoiceRootApi = cabinetRootApi + billingApi + invoice;
    String cabinetBillingAdminRootApi = cabinetRootApi + billingApi + admin + services;
    String cabinetBillingServicesRootApi = cabinetRootApi + billingApi + services;
    String cabinetBillingQuoteRootApi = cabinetRootApi + billingApi + quote;

    String mobileDashboardRootApi = mobileRootApi + dashboard;
    String terminalDashboardRootApi = terminalRootApi + dashboard;

    String cabinetPlanRouteDashboardRootApi = cabinetRootApi + planRouteDashboardApi;

    String cabinetPlanRouteRootApi = cabinetRootApi + planRouteApi;
    String mobilePlanRouteRootApi = mobileRootApi + planRouteApi;
    String publicPlanRouteRootApi = publicRootApi + planRouteApi;

    String mobilePlanRouteCustomerBranchRootApi = mobileRootApi + planRouteCustomerBranchApi;

    String cabinetPlanRouteTemplateRootApi = cabinetRootApi + planRouteTemplateApi;

    String mobileAgentTrackingRootApi = mobileRootApi + agentTrackingApi;
    String cabinetAgentTrackingRootApi = cabinetRootApi + agentTrackingApi;

    String adminNotificationTemplateRootApi = adminRootApi + notificationTemplateApi;

    String publicApplicationTypeRootApi = publicRootApi + applicationTypeApi;

    String adminNotificationRootApi = adminRootApi + notificationApi;
    String cabinetNotificationRootApi = cabinetRootApi + notificationApi;

    String cabinetTransactionRootApi = cabinetRootApi + transactionApi;

    String cabinetPriceListRootApi = cabinetRootApi + priceListApi;
    String mobilePriceListRootApi = mobileRootApi + priceListApi;

    String adminFileUploadRootApi = adminRootApi + fileUploadApi;

    String cabinetExportReportRootApi = cabinetRootApi + exportReportApi;

    String cabinetImportReportRootApi = cabinetRootApi + importReportApi;

    String cabinetCommittentRootApi = cabinetRootApi + committentApi;
    String mobileCommittentRootApi = mobileRootApi + committentApi;
    String adminCommittentRootApi = adminRootApi + committentApi;

    String cabinetStockReturnRootApi = cabinetRootApi + stockReturnApi;
    String terminalStockReturnRootApi = terminalRootApi + stockReturnApi;


    String cabinetFinancialOperationRootApi = cabinetRootApi + financialOperationApi;

    String cabinetProductSerialRootApi = cabinetRootApi + productSerialApi;

    String adminClickhouseRootApi = adminRootApi + clickhouse;

    String cabinetLogHistoryRootApi = cabinetRootApi + logHistory;
    String adminLogHistoryRootApi = adminRootApi + logHistory;

    String cabinetProductContractRootApi = cabinetRootApi + productContractApi;

    String cabinetPostRootApi = cabinetRootApi + postApi;
    String adminPostRootApi = adminRootApi + postApi;
    String publicPostRootApi = publicRootApi + postApi;

    String mobileCustomerBranchProductRootApi = mobileRootApi + customerBranchProduct;

    String adminGenericSettingsRootApi = adminRootApi + genericSettingsApi;
    String cabinetCompanySettingsRootApi = cabinetRootApi + companySettingsApi;
    String mobileCompanySettingsRootApi = mobileRootApi + companySettingsApi;
    String terminalCompanySettingsRootApi = terminalRootApi + companySettingsApi;
    String adminCompanySettingsRootApi = adminRootApi + companySettingsApi;
    String adminSettingsGroupRootApi = adminRootApi + settingsGroupApi;

    String cabinetCompanyDeliverySettingsRootApi = cabinetRootApi + companyDeliverySettingsApi;
    String cabinetCompanyPaymentSettingsRootApi = cabinetRootApi + companyPaymentSettingsApi;
    String cabinetMarketplaceSettingsRootApi = cabinetRootApi + marketplaceSettingsApi;

    String cabinetWaybillRootApi = cabinetRootApi + waybillApi;
    String adminWaybillRootApi = adminRootApi + waybillApi;

    String mobileSystemUpgradeRootApi = mobileRootApi + systemUpgradeApi;
    String adminSystemUpgradeRootApi = adminRootApi + systemUpgradeApi;

    String adminTerminalRootApi = adminRootApi + terminalApi;
    String cabinetTerminalRootApi = cabinetRootApi + terminalApi;
    String publicTerminalRootApi = publicRootApi + terminalApi;

    String adminTerminalLogRootApi = adminRootApi + terminalLogApi;

    String adminLauncherRootApi = adminRootApi + launcherApi;

    String cabinetDiscountRootApi = cabinetRootApi + discountApi;
    String mobileDiscountRootApi = mobileRootApi + discountApi;
    String terminalDiscountRootApi = terminalRootApi + discountApi;

    String adminStockOptimizationRootApi = adminRootApi + stockOptimizationApi;

    String cabinetVehicleRootApi = cabinetRootApi + vehicleApi;

    String cabinetContainerRootApi = cabinetRootApi + containerApi;

    String terminalLogRootApi = terminalRootApi + logApi;

    String cabinetCompetitorRootApi = cabinetRootApi + competitorProducts;

    String cabinetEquipmentRootApi = cabinetRootApi + equipmentApi;
    String mobileEquipmentRootApi = mobileRootApi + equipmentApi;

    String cabinetBranchBankRootApi = cabinetRootApi + branchBankApi;

    String cabinetCategorySortRootApi = cabinetRootApi + categorySortApi;

    String cabinetProductSortRootApi = cabinetRootApi + productSortApi;
    String mobileProductSortRootApi = mobileRootApi + productSortApi;

    String cabinetKPIRootApi = cabinetRootApi + KPI;
    String reconciliationActRootApi = cabinetRootApi + reconciliationAct;

    String Create = "Create";
    String Get = "Get";
    String Update = "Update";
    String Delete = "Delete";
    String List = "List";
    String Reverse = "Reverse";

    String Report = "Report";
    String ExportCSV = "Export CSV";
    String ExportExcel = "Export Excel";
    String Send = "Send";
    String Cancel = "Cancel";
    String Accept = "Accept";
    String Reject = "Reject";
    String LookUp = "Look Up";
    String Items = "Items";
    String Sync = "Sync";
    String All = "All";
    String Types = "Types";
    String Groups = "Groups";
    String UpdateStatus = "Update Status";
    String UpdateNumber = "Update Number";
    String Statuses = "Statuses";
    String Copy = "Copy";
    String Reasons = "Reasons";
    String Import = "Import";
    String Template = "Get Template";
    String Login = "Login";
    String Logout = "Logout";
    String Check = "Check";
    String Register = "Register";
    String Current = "Current User";
    String Activate = "Activate";
    String Roles = "Roles";
    String In = "In";
    String Out = "Out";
    String Statistics = "Statistics";
    String Stamp = "Stamp";
    String Withdraw = "Withdraw";
}
