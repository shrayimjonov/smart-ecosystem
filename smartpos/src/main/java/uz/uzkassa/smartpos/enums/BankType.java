package uz.uzkassa.smartpos.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BankType {
    TRASTBANK("Трастбанк"),
    HAMKORBANK("Хамкорбанк");

    private final String ruText;


}