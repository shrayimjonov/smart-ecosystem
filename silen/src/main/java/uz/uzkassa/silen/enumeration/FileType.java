package uz.uzkassa.silen.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Getter
@RequiredArgsConstructor
public enum FileType implements Serializable {
    EXCEL(".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    CSV(".csv", "text/csv"),
    JSON(".json", "application/json"),
    PDF(".pdf", "pdf");

    final String extension;
    final String contentType;
}
