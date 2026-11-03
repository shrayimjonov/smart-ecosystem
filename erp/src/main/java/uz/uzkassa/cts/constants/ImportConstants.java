package uz.uzkassa.cts.constants;

public interface ImportConstants {

    interface DEVICE {
        String SERIAL_NUMBER = "SERIAL_NUMBER";
        String MODEL_CODE = "MODEL_CODE";
    }

    interface FISCAL_MODULE {
        String SERIAL_NUMBER = "SERIAL_NUMBER";
    }

    interface CONTRACT_REPORT {
        String SERIAL_NUMBER = "SERIAL_NUMBER";
        String TIN = "TIN";
        String NAME = "NAME";
        String CONTRACT_NUMBER = "CONTRACT_NUMBER";
        String AMOUNT = "AMOUNT";
        String INVOICE_STATUS = "INVOICE_STATUS";
        String REALISATION = "REALISATION";
        String REGION_ID = "REGION_ID";
        String COMMENT = "COMMENT";
        String DOCUMENT_STATUS = "DOCUMENT_STATUS";
        String SALE_STATUS = "SALE_STATUS";
        String DEVICE_REPLACEMENT = "DEVICE_REPLACEMENT";
        String REASON = "REASON";
        String DATE = "DATE";
        String CHECK_STATUS = "CHECK_STATUS";
    }

    interface FISCAL_MODULE_REPORT {
        String TIN = "TIN";
        String DEVICE_SERIAL_NUMBER = "DEVICE_SERIAL_NUMBER";
        String FM_SERIAL_NUMBER = "FM_SERIAL_NUMBER";
        String FM_REPORT_REASON = "FM_REPORT_REASON";
        String ARRIVAL_STATUS = "ARRIVAL_STATUS";
        String SOLIQ_TRANSFER_STATUS = "SOLIQ_TRANSFER_STATUS";
    }
}
