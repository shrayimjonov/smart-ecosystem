package uz.uzkassa.silen.integration.asilbelgi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public enum MarkUnit implements Serializable {
    UNIT("Потребительская. Пачка/бутылка/штучная единица"),
    LEVEL1("Блок"),
    LEVEL2("Коробка"),
    LEVEL3("Паллет");

    private final String nameRu;
}
