package uz.uzkassa.cts.constants;

public interface FileConstants {
    String CONTRACT_OFFER_FILE_URL = "https://internal.example.com/cd0f0a6a-1a24-4b50-953a-3b5623640b84.pdf";
    String CONTRACT_OFFER_FILE_NAME = "Публичная_оферта.pdf";

    String CONTENT_TYPE_FOR_EXCEL = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    String CONTENT_TYPE_FOR_CSV = "text/csv";

    String STORAGE_BASE_URL = "https://internal.example.com/";


    int EXCEL_EXPORT_MAX_LIMIT = 1000000;
    int CSV_EXPORT_MAX_LIMIT = 1000000;
}
