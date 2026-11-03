package uz.uzkassa.horeca.constants;

public interface ApiConstants {
    String api = "/api";
    String admin = "/admin";
    String cabinet = "/cabinet";
    String mobile = "/mobile";
    String terminal = "/terminal";
    String pub = "/public";

    String version = "/v1";

    String adminRootApi = api + admin + version;
    String cabinetRootApi = api + cabinet + version;
    String mobileRootApi = api + mobile + version;
    String terminalRootApi = api + terminal + version;
    String publicRootApi = api + pub + version;

    //Actions
    String id = "/{id}";
    String lookUp = "/lookup";
    String batch = "/batch";
    String sync = "/sync";
    String statuses = "/statuses";
    String statistics = "/statistics";
    String roles = "/roles";
    String all = "/all";
    String check = "/check";
    String verify = "/verify";
    String register = "/register";
    String profile = "/profile";
    String login = "/login";
    String logout = "/logout";
    String updateNumber = "/{id}/number";
    String types = "/types";

    String resetPasswordInit = "/reset-password/init";
    String resetPasswordCheck = "/reset-password/check";
    String resetPasswordFinish = "/reset-password/finish";

    String resendResetPasswordKey = "/resend-reset-password-key";
    String resendActivationKey = "/resend-activation-key";

    String changePasswordInit = "/change-password/init";
    String changePasswordConfirm = "/change-password-confirm";

    String changeLoginInit = "/change-login/init";
    String changeLoginConfirm = "/change-login-confirm";

    String updateStatusBatch = "/batch-status";
    String updateStatus = "/{id}/status";
    String updateComment = "/{id}/comment";
    String items = "/items";
    String send = "/send";
    String tree = "/tree";
    String current = "/current";

    String webhookReceived = "/webhook/received";
    String webhookAccepted = "/webhook/accepted";
    String webhookAgentAccepted = "/webhook/agent-accepted";
    String webhookRejected = "/webhook/rejected";
    String webhookCancelled = "/webhook/cancelled";
    String webhookUpdated = "/webhook/updated";

    //APIs
    String companyApi = "/companies";
    String cateringServiceApi = "/catering-service";
    String companyBankApi = "/company-banks";
    String companySettingsApi = "/company-settings";
    String companyAddressApi = "/company-address";
    String bankApi = "/banks";
    String billApi = "/bills";
    String branchApi = "/branches";
    String systemUpgradeApi = "/system-upgrade";
    String customerApi = "/customers";
    String compensationApi = "/compensations";
    String hallApi = "/halls";
    String hallItemApi = "/hall-items";
    String hallEmployeeApi = "/hall-employees";
    String tableApi = "/tables";
    String departmentApi = "/departments";
    String activityTypeApi = "/activity-types";
    String districtApi = "/districts";
    String neighborhoodApi = "/neighborhoods";
    String employeeApi = "/employees";
    String userApi = "/users";
    String postApi = "/posts";
    String productApi = "/products";
    String techCardApi = "/tech-cards";
    String unitApi = "/units";
    String receiptApi = "/receipts";
    String regionApi = "/regions";
    String categoryApi = "/categories";
    String permissionApi = "/permissions";
    String authenticateApi = "/authenticate";
    String accountApi = "/account";
    String userSessionApi = "/user-session";
    String commonApi = "/commons";
    String cacheApi = "/caches";
    String orderApi = "/orders";
    String menuApi = "/menus";
    String menuItemApi = "/menu-items";
    String multiUnitApi = "/multi-units";
    String currentStockApi = "/current-stock";
    String warehouseApi = "/warehouses";
    String terminalApi = "/terminals";
    String warehouseItemApi = "/warehouse-items";
    String stockInApi = "/stock-in";
    String stockOutApi = "/stock-out";
    String stockTransferApi = "/stock-transfers";
    String stockTransferItemsApi = "/items";
    String stockAdjustmentApi = "/stock-adjustments";
    String orderItemApi = "/order-items";
    String reservationApi = "/reservations";
    String securityApi = "/securities";
    String supplierApi = "/suppliers";
    String facturaApi = "/factura";
    String invoiceApi = "/invoices";
    String logHistoryApi = "/log-history";
    String epsServiceApi = "/eps-service";
    String shiftApi = "/shift";
    String billingApi = "/billing";
    String invoice = "/invoice";
    String services = "/services";
    String quote = "/quote";
    String reference = "/reference";
    String socialAddress = "/social-address";
    String qrMenu = "/qr-menu";
    String apay = "/apay";
    //Swagger
    String swaggerRootApi = api;

    String publicActivityTypeRootApi = publicRootApi + activityTypeApi;

    String cabinetBankRootApi = cabinetRootApi + bankApi;
    String adminAccountRootApi = adminRootApi + accountApi;

    String adminReceiptRooApi = adminRootApi + receiptApi;
    String cabinetReceiptRooApi = cabinetRootApi + receiptApi;
    String mobileReceiptRooApi = mobileRootApi + receiptApi;
    String terminalReceiptRooApi = terminalRootApi + receiptApi;

    String adminPostRootApi = adminRootApi + postApi;
    String adminPermissionRootApi = adminRootApi + permissionApi;
    String cabinetPermissionRootApi = cabinetRootApi + permissionApi;
    String adminCacheRootApi = adminRootApi + cacheApi;
    String adminSecurityRootApi = adminRootApi + securityApi;

    String cabinetAccountRootApi = cabinetRootApi + accountApi;
    String mobileAccountRootApi = mobileRootApi + accountApi;

    String mobileUserSessionRootApi = mobileRootApi + userSessionApi;

    String cabinetOrderRootApi = cabinetRootApi + orderApi;
    String adminOrderRootApi = adminRootApi + orderApi;
    String mobileOrderRootApi = mobileRootApi + orderApi;

    String cabinetOrderItemRootApi = cabinetRootApi + orderItemApi;
    String mobileOrderItemRootApi = mobileRootApi + orderItemApi;
    String cabinetReservationRootApi = cabinetRootApi + reservationApi;
    String adminReservationRootApi = adminRootApi + reservationApi;
    String mobileReservationRootApi = mobileRootApi + reservationApi;
    String cabinetPostRootApi = cabinetRootApi + postApi;

    String cabinetWarehouseRootApi = cabinetRootApi + warehouseApi;
    String adminWarehouseRootApi = adminRootApi + warehouseApi;

    String cabinetTerminalRootApi = cabinetRootApi + terminalApi;
    String mobileTerminalRootApi = mobileRootApi + terminalApi;
    String adminTerminalRootApi = adminRootApi + terminalApi;

    String cabinetWarehouseItemRootApi = cabinetRootApi + warehouseItemApi;

    String adminCategoryRootApi = adminRootApi + categoryApi;
    String cabinetCategoryRootApi = cabinetRootApi + categoryApi;

    String adminEmployeeRootApi = adminRootApi + employeeApi;
    String cabinetEmployeeRootApi = cabinetRootApi + employeeApi;

    String cabinetUserRootApi = cabinetRootApi + userApi;
    String adminUserRootApi = adminRootApi + userApi;

    String adminCompanyRootApi = adminRootApi + companyApi;
    String cabinetCompanyRootApi = cabinetRootApi + companyApi;

    String cabinetCompanyBankRootApi = cabinetRootApi + companyBankApi;
    String cabinetCompanyAddressRootApi = cabinetRootApi + companyAddressApi;
    String cabinetCompanySettingsRootApi = cabinetRootApi + companySettingsApi;

    String adminBranchRootApi = adminRootApi + branchApi;
    String cabinetBranchRootApi = cabinetRootApi + branchApi;
    String cabinetCateringServiceRootApi = cabinetRootApi + cateringServiceApi;

    String adminSystemUpgradeRootApi = adminRootApi + systemUpgradeApi;
    String cabinetSystemUpgradeRootApi = cabinetRootApi + systemUpgradeApi;
    String mobileSystemUpgradeRootApi = mobileRootApi + systemUpgradeApi;

    String cabinetCustomerRootApi = cabinetRootApi + customerApi;
    String mobileCustomerRootApi = mobileRootApi + customerApi;

    String cabinetCompensationRootApi = cabinetRootApi + compensationApi;

    String cabinetHallRootApi = cabinetRootApi + hallApi;
    String adminHallRootApi = adminRootApi + hallApi;
    String mobileHallRootApi = mobileRootApi + hallApi;

    String cabinetHallEmployeeRootApi = cabinetRootApi + hallEmployeeApi;

    String cabinetHallItemRootApi = cabinetRootApi + hallItemApi;
    String adminHallItemRootApi = adminRootApi + hallItemApi;
    String mobileHallItemRootApi = mobileRootApi + hallItemApi;

    String cabinetTableRootApi = cabinetRootApi + tableApi;
    String adminTableRootApi = adminRootApi + tableApi;
    String mobileTableRootApi = mobileRootApi + tableApi;
    String terminalTableRootApi = terminalRootApi + tableApi;

    String adminDepartmentRootApi = adminRootApi + departmentApi;
    String cabinetDepartmentRootApi = cabinetRootApi + departmentApi;

    String adminProductRootApi = adminRootApi + productApi;
    String cabinetProductRootApi = cabinetRootApi + productApi;
    String mobileProductRootApi = mobileRootApi + productApi;

    String cabinetTechCardRootApi = cabinetRootApi + techCardApi;
    String adminTechCardRootApi = adminRootApi + techCardApi;

    String cabinetStockInRootApi = cabinetRootApi + stockInApi;

    String cabinetStockOutRootApi = cabinetRootApi + stockOutApi;

    String cabinetStockTransferRootApi = cabinetRootApi + stockTransferApi;
    String cabinetStockAdjustmentRootApi = cabinetRootApi + stockAdjustmentApi;

    String cabinetStockTransferItemsRootApi = cabinetRootApi + stockTransferApi + stockTransferItemsApi;

    String cabinetSupplierRootApi = cabinetRootApi + supplierApi;

    String cabinetMenuRootApi = cabinetRootApi + menuApi;
    String mobileMenuRootApi = mobileRootApi + menuApi;
    String adminMenuRootApi = adminRootApi + menuApi;

    String cabinetMenuItemRootApi = cabinetRootApi + menuItemApi;
    String terminalMenuItemRootApi = terminalRootApi + menuItemApi;
    String mobileMenuItemRootApi = mobileRootApi + menuItemApi;
    String adminMenuItemRootApi = adminRootApi + menuItemApi;

    String mobileMultiUnitRootApi = mobileRootApi + multiUnitApi;

    String cabinetCurrentStockRootApi = cabinetRootApi + currentStockApi;

    String cabinetFacturaRootApi = cabinetRootApi + facturaApi;
    String cabinetInvoiceRootApi = cabinetRootApi + invoiceApi;
    String cabinetBillRootApi = cabinetRootApi + billApi;
    String adminBillRootApi = adminRootApi + billApi;
    String mobileBillRootApi = mobileRootApi + billApi;

    String publicUnitRootApi = publicRootApi + unitApi;
    String publicRegionRootApi = publicRootApi + regionApi;
    String publicDistrictRootApi = publicRootApi + districtApi;
    String publicNeighborhoodRootApi = publicRootApi + neighborhoodApi;
    String publicAccountRootApi = publicRootApi + accountApi;
    String publicCommonRootApi = publicRootApi + commonApi;

    String adminLogHistoryRootApi = adminRootApi + logHistoryApi;

    String cabinetEpsServiceRootApi = cabinetRootApi + epsServiceApi;
    String mobileEpsServiceRootApi = mobileRootApi + epsServiceApi;

    String cabinetShiftRootApi = cabinetRootApi + shiftApi;
    String mobileShiftRootApi = mobileRootApi + shiftApi;

    String cabinetBillingInvoiceRootApi = cabinetRootApi + billingApi + invoice;
    String cabinetBillingAdminRootApi = cabinetRootApi + billingApi + admin + services;
    String cabinetBillingServicesRootApi = cabinetRootApi + billingApi + services;
    String cabinetBillingQuoteRootApi = cabinetRootApi + billingApi + quote;

    String cabinetReferenceRootApi = cabinetRootApi + reference;
    String adminReferenceRootApi = adminRootApi + reference;
    String cabinetSocialAddressRootApi = cabinetRootApi + socialAddress;
    String adminSocialAddressRootApi = adminRootApi + socialAddress;
    String publicQrMenuRootApi = publicRootApi + qrMenu;
    String mobileApayRootApi = mobileRootApi + apay;

}
