package uz.uzkassa.smartpossupply.constants;

public interface Constants {
    // Regex for acceptable logins
    String LOGIN_REGEX = "^(?>[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*)|(?>[_.@A-Za-z0-9-]+)$";
    String PHONE_REGEX = "^998[\\d]{9}$";
    String TIN_REGEX = "[0-9]{9,14}";
    int EXCEL_EXPORT_MAX_LIMIT = 1000000;
    int CSV_EXPORT_MAX_LIMIT = 1000000;

    String INCREMENT_NUMBER = "INCREMENT_NUMBER";
    String NUMBER = "NUMBER";

    int MAX_MARK_QTY_FOR_RECEIPT = 3000;

    int MAX_ROW_COUNT_TO_DELETE_IN_PER_QUERY = 10000;
    int DURATION_OF_DELETED_CODES_SAVING_MEASURED_IN_DAYS = 30;
    int ACCOUNT_RECEIVABLE_REPORT_IMPORT_LIMIT = 10000;
    int COMMON_BATCH_SIZE = 1000;
}
