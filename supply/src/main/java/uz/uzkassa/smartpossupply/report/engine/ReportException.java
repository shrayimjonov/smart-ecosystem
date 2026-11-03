package uz.uzkassa.smartpossupply.report.engine;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import uz.uzkassa.smartpossupply.web.rest.errors.AppGlobalException;

@Getter
public class ReportException extends AppGlobalException {

    private final ReportErrorCode code;

    private ReportException(ReportErrorCode code, String title, String message, HttpStatus status) {
        super(title, message, status);
        this.code = code;
    }

    public static ReportException validation(ReportErrorCode code, String message) {
        return new ReportException(code, "Некорректный запрос отчёта", message, HttpStatus.BAD_REQUEST);
    }

    public static ReportException forbidden(ReportErrorCode code, String message) {
        return new ReportException(code, "Доступ к отчёту запрещён", message, HttpStatus.FORBIDDEN);
    }

    public static ReportException limit(ReportErrorCode code, String message) {
        return new ReportException(code, "Превышен лимит отчёта", message, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
