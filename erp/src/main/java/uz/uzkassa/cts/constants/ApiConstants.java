package uz.uzkassa.cts.constants;

public interface ApiConstants {
    String api = "/api";
    String admin = "/admin";
    String cabinet = "/cabinet";
    String mobile = "/mobile";
    String pub = "/public";

    String version = "/v1";

    String adminRootApi = api + admin + version;
    String cabinetRootApi = api + cabinet + version;
    String mobileRootApi = api + mobile + version;
    String publicRootApi = api + pub + version;

    //Actions
    String id = "/{id}";
    String lookUp = "/lookup";
    String batch = "/batch";
    String tree = "/tree";
    String sync = "/sync";
    String statuses = "/statuses";
    String merge = "/merge";
    String roles = "/roles";
    String types = "/types";
    String all = "/all";
    String check = "/check";
    String verify = "/verify";
    String register = "/register";
    String profile = "/profile";
    String login = "/login";
    String logout = "/logout";
    String upload = "/upload";
    String statistics = "/statistics";
    String idStatistics = "/{id}/statistics";
    String download = "/download";
    String assignMaster = "/{id}/master";
    String transfer = "/{id}/transfer";
    String accept = "/{id}/accept";
    String updateType = "/{id}/type";
    String updateRepairType = "/{id}/repair-type";
    String equipment = "/{id}/equipments";
    String visitTypes = "/visit-types";
    String repairTypes = "/repair-types";

    String changePasswordInit = "/change-password/init";
    String changePasswordConfirm = "/change-password/confirm";

    String changeUsernameInit = "/change-username/init";
    String changeUsernameConfirm = "/change-username/confirm";

    String updateStatus = "/{id}/status";
    String uploadFile = "/{id}/upload";
    String items = "/items";
    String send = "/send";

    String exportExcel = "/export-excel";
    String exportCSV = "/export-csv";

    String importExcel = "/import-excel";
    String importCSV = "/import-csv";

    String downloadExcelTemplate = "/download-excel-template";
    String downloadCsvTemplate = "/download-csv-template";

    String current = "/current";
    String activate = "/activate";
    String activateFinish = "/activate/finish";
    String sendActivationCode = "/send-activation-code";
    String template = "/template";
    String assignHr = "/{id}/assign-hr";


    //APIs
    String companyApi = "/companies";
    String bankApi = "/banks";
    String socialAddressApi = "/social-addresses";
    String branchApi = "/branches";
    String emailApi = "/emails";
    String smsApi = "/sms";
    String brandApi = "/brands";
    String notificationApi = "/notifications";
    String apayCategoryApi = "/apay-categories";
    String vatApi = "/vats";
    String departmentApi = "/departments";
    String deviceApi = "/devices";
    String companyDeviceApi = "/company-devices";
    String approvalProcessApi = "/approval-process";
    String approverApi = "/approvers";
    String approvalProcessHistoryApi = "/approval-process-history";

    String branchDeviceApi = "/branch-devices";
    String customerDeviceApi = "/customer-devices";
    String customerBranchDeviceApi = "/customer-branch-devices";
    String fiscalModuleApi = "/fiscal-modules";
    String companyFiscalModuleApi = "/company-fiscal-modules";
    String branchFiscalModuleApi = "/branch-fiscal-modules";
    String customerFiscalModuleApi = "/customer-fiscal-modules";
    String customerBranchFiscalModuleApi = "/customer-branch-fiscal-modules";

    String companyStructureApi = "/company-structures";
    String groupApi = "/groups";
    String folderApi = "/folders";
    String templateApi = "/templates";
    String activityTypeApi = "/activity-types";
    String districtApi = "/districts";
    String neighborhoodApi = "/neighborhoods";
    String customerBranchApplicationApi = "/customer-branch-applications";
    String customerBankApi = "/customer-banks";
    String customerEmployeeApi = "/customer-employees";
    String fileApi = "/files";
    String timeslotApi = "/timeslots";
    String positionApi = "/positions";
    String postfixApi = "/postfix";
    String employeeApi = "/employees";
    String noteApi = "/notes";
    String employeeTimeslotApi = "/employee-timeslots";
    String userApi = "/users";
    String productApi = "/products";
    String productPhotoApi = "/product-photos";
    String stockInApi = "/stock-in";
    String stockOutApi = "/stock-out";
    String stockReturnApi = "/stock-return";
    String regionApi = "/regions";
    String categoryApi = "/categories";
    String categoryFieldApi = "/category-fields";
    String productVariationApi = "/product-variations";
    String variationTypeApi = "/variation-types";
    String variationOptionApi = "/variation-options";
    String accountApi = "/account";
    String commonApi = "/commons";
    String cacheApi = "/caches";
    String warehouseApi = "/warehouses";
    String warehouseItemApi = "/warehouse-items";
    String roleApi = "/roles";
    String referenceApi = "/references";
    String customerApi = "/customers";
    String customerSynApi = "/customers-sync";
    String fiscalizationSynApi = "/fiscalizations-sync";
    String fiscalizationApi = "/fiscalizations";
    String customerBranchSynApi = "/customer-branches-sync";
    String customerBranchApi = "/customer-branches";
    String vacancyApi = "/vacancies";
    String vacancyApplicationApi = "/vacancy-applications";
    String vacancyApplicationTemplateApi = "/vacancy-application-templates";
    String candidateApi = "/candidates";
    String dependentApi = "/dependents";
    String ofdApplicationApi = "/ofd-applications";
    String ofdCustomerBranchApi = "/ofd-customer-branches";
    String ofdCustomerApi = "/ofd-customers";
    String uzcardConnectionApi = "/uzcard-connection";
    String changeLogApi = "/changes-logs";
    String feedbackApi = "/feedbacks";
    String customerRequestApi = "/customer-requests";
    String leadApi = "/leads";
    String permissionApi = "/permissions";
    String userPermissionApi = "/user-permissions";
    String rolePermissionApi = "/role-permissions";
    String stockTransferApi = "/stock-transfers";
    String exportReportApi = "/export-reports";
    String importReportApi = "/import-reports";
    String integrationApi = "/integrations";
    String mailingListApi = "/mailing-list";
    String mailingListItemApi = "/mailing-list-items";
    String scheduleMessageApi = "/schedule-messages";
    String timeTrackApi = "/time-tracks";
    String attendanceApi = "/attendances";
    String benefitRequestApi = "/benefit-requests";
    String equipmentRequestApi = "/equipment-requests";
    String eventApi = "/event";
    String eventRequestApi = "/event-requests";
    String promoManagerDashboardApi = "/promo-manager-dashboard";
    String feedbackCountByTypeApi = "/feedback-count-by-type";
    String feedbackCountByStatusApi = "/feedback-count-by-status";
    String feedbackCountBydMonthApi = "/feedback-count-by-month";
    String customerApplicationCountByMonthApi = "/customer-application-count-by-month";
    String regionRateByFeedbackApi = "/region-rate-by-feedback";
    String topRegionsByFeedbackApi = "/top-regions-by-feedback";
    String topRegionsByCustomerApplicationApi = "/top-regions-by-applications";
    String repairTicketApi = "/repair-tickets";
    String repairTicketSettingsApi = "/repair-ticket-settings";
    String stockAdjustmentApi = "/stock-adjustments";
    String caseXizmatApi = "/case-xizmats";
    String holidayApi = "/holidays";
    String leaveRequestApi = "/leave-requests";
    String salaryAllocationApi = "/salary-allocations";
    String vehicleApi = "/vehicles";
    String addressApi = "/addresses";
    String personalIdentityInformationApi = "/personal-identity-information";
    String employmentInformationApi = "/employment-information";
    String leaveRequestReasonApi = "/leave-request-reasons";
    String leaveAllowanceApi = "/leave-allowances";
    String chatApi = "/chats";
    String documentApi = "/documents";
    String regionalAssignmentApi = "/regional-assignments";
    String serviceOrderInvoiceApi = "/service-order-invoices";
    String oneTimeOrderInvoiceApi = "/one-time-order-invoices";
    String actOfTransmissionInvoiceApi = "/act-of-transmission-invoices";
    String tradeInInvoiceApi = "/trade-in-invoices";
    String saleInvoiceApi = "/sale-invoices";
    String deviceSuspendHistoryApi = "/device-suspend-history";
    String whiteListApi = "/white-lists";
    String accessoryApi = "/accessories";
    String accessoryModelApi = "/accessory-models";
    String serviceOrderSpecificationApi = "/service-order-specifications";
    String bonusApi = "/bonuses";
    String fiscalModuleReportApi = "/fiscal-module-reports";
    String oneTimeOrderApi = "/one-time-orders";
    String caseStatusLogApi = "/case-status-logs";


    //Swagger

    String cabinetAccountRootApi = cabinetRootApi + accountApi;
    String adminAccountRootApi = adminRootApi + accountApi;
    String publicAccountRootApi = publicRootApi + accountApi;

    String adminExportReportRootApi = adminRootApi + exportReportApi;
    String adminImportReportRootApi = adminRootApi + importReportApi;

    String publicActivityTypeRootApi = publicRootApi + activityTypeApi;

    String cabinetBankRootApi = cabinetRootApi + bankApi;
    String adminBankRootApi = adminRootApi + bankApi;

    String publicReferenceRootApi = publicRootApi + referenceApi;
    String adminReferenceRootApi = adminRootApi + referenceApi;

    String cabinetSocialAddressRootApi = cabinetRootApi + socialAddressApi;

    String cabinetWarehouseRootApi = cabinetRootApi + warehouseApi;
    String adminWarehouseRootApi = adminRootApi + warehouseApi;

    String adminWarehouseItemRootApi = adminRootApi + warehouseItemApi;

    String adminRoleRootApi = adminRootApi + roleApi;
    String cabinetRoleRootApi = cabinetRootApi + roleApi;

    String adminCategoryRootApi = adminRootApi + categoryApi;
    String adminCategoryFieldRootApi = adminRootApi + categoryFieldApi;

    String adminVariationTypeRootApi = adminRootApi + variationTypeApi;
    String adminVariationOptionRootApi = adminRootApi + variationOptionApi;
    String adminProductVariationRootApi = adminRootApi + productVariationApi;

    String adminEmployeeTimeslotRootApi = adminRootApi + employeeTimeslotApi;

    String adminEmployeeRootApi = adminRootApi + employeeApi;
    String cabinetEmployeeRootApi = cabinetRootApi + employeeApi;

    String adminNoteRootApi = adminRootApi + noteApi;
    String cabinetNoteRootApi = cabinetRootApi + noteApi;

    String adminUserRootApi = adminRootApi + userApi;

    String adminCompanyRootApi = adminRootApi + companyApi;

    String adminNotificationRootApi = adminRootApi + notificationApi;
    String cabinetNotificationRootApi = cabinetRootApi + notificationApi;

    String adminBranchRootApi = adminRootApi + branchApi;
    String cabinetBranchRootApi = cabinetRootApi + branchApi;

    String adminBrandRootApi = adminRootApi + brandApi;
    String cabinetBrandRootApi = cabinetRootApi + brandApi;

    String adminApayCategoryRootApi = adminRootApi + apayCategoryApi;
    String cabinetApayCategoryRootApi = cabinetRootApi + apayCategoryApi;

    String adminCustomerBankRootApi = adminRootApi + customerBankApi;
    String cabinetCustomerBankRootApi = cabinetRootApi + customerBankApi;

    String adminCustomerEmployeeRootApi = adminRootApi + customerEmployeeApi;
    String cabinetCustomerEmployeeRootApi = cabinetRootApi + customerEmployeeApi;

    String adminDepartmentRootApi = adminRootApi + departmentApi;
    String cabinetDepartmentRootApi = cabinetRootApi + departmentApi;

    String adminDeviceRootApi = adminRootApi + deviceApi;
    String adminCompanyDeviceRootApi = adminRootApi + companyDeviceApi;
    String adminBranchDeviceRootApi = adminRootApi + branchDeviceApi;
    String adminCustomerDeviceRootApi = adminRootApi + customerDeviceApi;
    String adminCustomerBranchDeviceRootApi = adminRootApi + customerBranchDeviceApi;

    String cabinetCustomerBranchDeviceRootApi = cabinetRootApi + customerBranchDeviceApi;

    String adminFiscalModuleRootApi = adminRootApi + fiscalModuleApi;
    String adminCompanyFiscalModuleRootApi = adminRootApi + companyFiscalModuleApi;
    String adminBranchFiscalModuleRootApi = adminRootApi + branchFiscalModuleApi;
    String adminCustomerFiscalModuleRootApi = adminRootApi + customerFiscalModuleApi;
    String adminCustomerBranchFiscalModuleRootApi = adminRootApi + customerBranchFiscalModuleApi;

    String adminCompanyStructureRootApi = adminRootApi + companyStructureApi;
    String adminApprovalProcessRootApi = adminRootApi + approvalProcessApi;

    String adminGroupRootApi = adminRootApi + groupApi;

    String adminFolderRootApi = adminRootApi + folderApi;
    String cabinetFolderRootApi = cabinetRootApi + folderApi;

    String adminTemplateRootApi = adminRootApi + templateApi;

    String adminProductRootApi = adminRootApi + productApi;
    String adminProductPhotoRootApi = adminRootApi + productPhotoApi;

    String adminEmailRootApi = adminRootApi + emailApi;
    String adminSmsRootApi = adminRootApi + smsApi;

    String cabinetFileRootApi = cabinetRootApi + fileApi;
    String adminFileRootApi = adminRootApi + fileApi;

    String adminVatRootApi = adminRootApi + vatApi;
    String publicVatRootApi = publicRootApi + vatApi;

    String adminCacheRootApi = adminRootApi + cacheApi;

    String adminStockInRootApi = adminRootApi + stockInApi;
    String adminStockOutRootApi = adminRootApi + stockOutApi;
    String adminStockReturnRootApi = adminRootApi + stockReturnApi;

    String cabinetPositionRootApi = cabinetRootApi + positionApi;
    String adminPositionRootApi = adminRootApi + positionApi;

    String adminPostfixRootApi = adminRootApi + postfixApi;

    String cabinetCustomerRootApi = cabinetRootApi + customerApi;
    String adminCustomerRootApi = adminRootApi + customerApi;

    String adminCustomerSyncRootApi = adminRootApi + customerSynApi;
    String adminFiscalizationSyncRootApi = adminRootApi + fiscalizationSynApi;

    String adminFiscalizationRootApi = adminRootApi + fiscalizationApi;

    String adminCustomerBranchRootApi = adminRootApi + customerBranchApi;
    String cabinetCustomerBranchRootApi = cabinetRootApi + customerBranchApi;

    String adminCustomerBranchSyncRootApi = adminRootApi + customerBranchSynApi;

    String adminOfdApplicationRootApi = adminRootApi + ofdApplicationApi;
    String cabinetOfdApplicationRootApi = cabinetRootApi + ofdApplicationApi;

    String adminOfdCustomerBranchRootApi = adminRootApi + ofdCustomerBranchApi;
    String adminOfdCustomerRootApi = adminRootApi + ofdCustomerApi;

    String adminChangeLogRootApi = adminRootApi + changeLogApi;
    String adminUzcardConnectionRootApi = adminRootApi + uzcardConnectionApi;

    String cabinetTimeslotRootApi = cabinetRootApi + timeslotApi;
    String adminTimeslotRootApi = adminRootApi + timeslotApi;

    String publicRegionRootApi = publicRootApi + regionApi;
    String publicDistrictRootApi = publicRootApi + districtApi;
    String publicNeighborhoodRootApi = publicRootApi + neighborhoodApi;
    String adminNeighborhoodRootApi = adminRootApi + neighborhoodApi;

    String adminCustomerBranchApplicationRootApi = adminRootApi + customerBranchApplicationApi;
    String cabinetCustomerBranchApplicationRootApi = cabinetRootApi + customerBranchApplicationApi;

    String adminCommonRootApi = adminRootApi + commonApi;
    String publicCommonRootApi = publicRootApi + commonApi;

    String adminCandidateRootApi = adminRootApi + candidateApi;
    String adminDependentRootApi = adminRootApi + dependentApi;

    String adminVacancyRootApi = adminRootApi + vacancyApi;
    String adminVacancyApplicationRootApi = adminRootApi + vacancyApplicationApi;

    String adminVacancyApplicationTemplateRootApi = adminRootApi + vacancyApplicationTemplateApi;

    String adminFeedbackRootApi = adminRootApi + feedbackApi;
    String cabinetFeedbackRootApi = cabinetRootApi + feedbackApi;

    String adminCustomerRequestRootApi = adminRootApi + customerRequestApi;

    String adminLeadRootApi = adminRootApi + leadApi;
    String cabinetLeadRootApi = cabinetRootApi + leadApi;

    String adminPermissionRootApi = adminRootApi + permissionApi;

    String adminUserPermissionRootApi = adminRootApi + userPermissionApi;
    String cabinetUserPermissionRootApi = cabinetRootApi + userPermissionApi;

    String adminRolePermissionRootApi = adminRootApi + rolePermissionApi;

    String adminIntegrationRootApi = adminRootApi + integrationApi;

    String adminMailingListRootApi = adminRootApi + mailingListApi;
    String adminMailingListItemRootApi = adminRootApi + mailingListItemApi;

    String adminScheduleMessageRootApi = adminRootApi + scheduleMessageApi;

    String adminTimeTrackRootApi = adminRootApi + timeTrackApi;
    String adminAttendanceApi = adminRootApi + attendanceApi;

    String adminBenefitRequestRootApi = adminRootApi + benefitRequestApi;
    String cabinetBenefitRequestRootApi = cabinetRootApi + benefitRequestApi;

    String adminEquipmentRequestRootApi = adminRootApi + equipmentRequestApi;
    String cabinetEquipmentRequestRootApi = cabinetRootApi + equipmentRequestApi;

    String adminEventRootApi = adminRootApi + eventApi;

    String adminEventRequestRootApi = adminRootApi + eventRequestApi;

    String adminPromoManagerDashboardRootApi = adminRootApi + promoManagerDashboardApi;

    String adminRepairTicketRootApi = adminRootApi + repairTicketApi;

    String adminRepairTicketSettingsRootApi = adminRootApi + repairTicketSettingsApi;

    String adminStockAdjustmentRootApi = adminRootApi + stockAdjustmentApi;

    String adminStockTransferRootApi = adminRootApi + stockTransferApi;

    String adminCaseXizmatRootApi = adminRootApi + caseXizmatApi;

    String adminHolidayRootApi = adminRootApi + holidayApi;

    String adminLeaveRequestRootApi = adminRootApi + leaveRequestApi;

    String adminApproverRootApi = adminRootApi + approverApi;

    String adminApprovalProcessHistoryRootApi = adminRootApi + approvalProcessHistoryApi;

    String adminSalaryAllocationRootApi = adminRootApi + salaryAllocationApi;

    String adminVehicleRootApi = adminRootApi + vehicleApi;

    String adminAddressRootApi = adminRootApi + addressApi;

    String adminPersonalIdentityInformationRootApi = adminRootApi + personalIdentityInformationApi;

    String adminEmploymentInformationRootApi = adminRootApi + employmentInformationApi;

    String adminLeaveRequestReasonRootApi = adminRootApi + leaveRequestReasonApi;

    String adminLeaveAllowanceRootApi = adminRootApi + leaveAllowanceApi;

    String adminChatRootApi = adminRootApi + chatApi;

    String adminDocumentRootApi = adminRootApi + documentApi;
    String cabinetDocumentRootApi = cabinetRootApi + documentApi;

    String adminServiceOrderInvoiceRootApi = adminRootApi + serviceOrderInvoiceApi;

    String adminOneTimeOrderInvoiceRootApi = adminRootApi + oneTimeOrderInvoiceApi;

    String adminActOfTransmissionInvoiceRootApi = adminRootApi + actOfTransmissionInvoiceApi;

    String adminTradeInInvoiceRootApi = adminRootApi + tradeInInvoiceApi;

    String adminSaleInvoiceRootApi = adminRootApi + saleInvoiceApi;

    String adminDeviceSuspendHistoryRootApi = adminRootApi + deviceSuspendHistoryApi;

    String adminWhiteListRootApi = adminRootApi + whiteListApi;
    String adminRegionalAssignmentRootApi = adminRootApi + regionalAssignmentApi;

    String adminAccessoryRootApi = adminRootApi + accessoryApi;
    String cabinetAccessoryRootApi = cabinetRootApi + accessoryApi;

    String adminAccessoryModelRootApi = adminRootApi + accessoryModelApi;
    String cabinetAccessoryModelRootApi = cabinetRootApi + accessoryModelApi;

    String adminServiceOrderSpecificationRootApi = adminRootApi + serviceOrderSpecificationApi;

    String adminBonusRootApi = adminRootApi + bonusApi;

    String adminOneTimeOrderRootApi = adminRootApi + oneTimeOrderApi;

    String adminFiscalModuleReportRootApi = adminRootApi + fiscalModuleReportApi;

    String adminCaseStatusLogRootApi = adminRootApi + caseStatusLogApi;

}
