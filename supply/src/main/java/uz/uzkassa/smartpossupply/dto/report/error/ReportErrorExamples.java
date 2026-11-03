package uz.uzkassa.smartpossupply.dto.report.error;

import uz.uzkassa.smartpossupply.constants.ApiConstants;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Deterministic OpenAPI examples that follow the existing ErrorDTO JSON shape.
 */
public final class ReportErrorExamples {

    private static final String PREVIEW_PATH = ApiConstants.cabinetReportRootApi + ApiConstants.reportPreview;
    private static final String DATASETS_PATH = ApiConstants.cabinetReportRootApi + ApiConstants.reportDatasets;
    private static final String TIMESTAMP = "2026-10-07T05:00:00";

    private ReportErrorExamples() {
    }

    public static Map<String, Object> validation() {
        Map<String, Object> example = error(
                "Некорректный запрос",
                "Заполните обязательные поля",
                400,
                PREVIEW_PATH
        );
        example.put("errorFields", List.of(Map.of(
                "objectName", "ReportPreviewRequest",
                "field", "period.from",
                "message", "Поле обязательно"
        )));
        return example;
    }

    public static Map<String, Object> unauthorizedPreview() {
        return error(
                "Требуется авторизация",
                "Войдите в систему для работы с отчётами",
                401,
                PREVIEW_PATH
        );
    }

    public static Map<String, Object> unauthorizedDatasets() {
        return error(
                "Требуется авторизация",
                "Войдите в систему для работы с отчётами",
                401,
                DATASETS_PATH
        );
    }

    public static Map<String, Object> forbiddenPreview() {
        return error(
                "Доступ запрещён",
                "Недостаточно прав для построения отчёта",
                403,
                PREVIEW_PATH
        );
    }

    public static Map<String, Object> forbiddenDatasets() {
        return error(
                "Доступ запрещён",
                "Недостаточно прав для просмотра датасетов",
                403,
                DATASETS_PATH
        );
    }

    public static Map<String, Object> maxCellsExceeded() {
        return error(
                "Превышен лимит отчёта",
                "Результат содержит больше 50 000 ячеек. Сузьте период или уберите измерение",
                422,
                PREVIEW_PATH
        );
    }

    public static Map<String, Object> maxRowsExceeded() {
        return error(
                "Превышен лимит отчёта",
                "Результат содержит больше 100 000 строк. Сузьте период или уберите измерение",
                422,
                PREVIEW_PATH
        );
    }

    public static Map<String, Object> timeoutExceeded() {
        return error(
                "Превышен лимит отчёта",
                "Время выполнения отчёта превысило допустимый лимит",
                422,
                PREVIEW_PATH
        );
    }

    public static Map<String, Object> concurrentPreviewsExceeded() {
        return error(
                "Превышен лимит отчёта",
                "Превышен лимит одновременно выполняемых отчётов для компании",
                422,
                PREVIEW_PATH
        );
    }

    private static Map<String, Object> error(String title, String detail, int status, String path) {
        Map<String, Object> example = new LinkedHashMap<>();
        example.put("title", title);
        example.put("detail", detail);
        example.put("status", status);
        example.put("timestamp", TIMESTAMP);
        example.put("path", path);
        example.put("refreshToken", false);
        return example;
    }
}
