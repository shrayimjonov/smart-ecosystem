package uz.uzkassa.smartpos.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum Emoji {
    UZ_FLAG("\uD83C\uDDFA\uD83C\uDDFF "),
    RU_FLAG("\uD83C\uDDF7\uD83C\uDDFA "),
    HOME("\uD83C\uDFE0️ "),
    BACK("⬅️️ "),
    SETTINGS("⚙️ "),
    HELP("\uD83C\uDD98️ "),
    BALANCE("\uD83D\uDCB0️"),
    MONEY("\uD83D\uDCB8️"),
    CASH("\uD83D\uDCB5️ "),
    CREDIT_CARD("\uD83D\uDCB3️"),
    HANDPHONE("\uD83D\uDCDE  "),
    PHONE("☎️ "),
    PHONE_NEW("\uD83D\uDCF2️ "),
    ALARM_CLOCK("⏰️ "),
    CLOCK("⏱️ "),
    HOURGLASS("⌛️️ "),
    CHECK("✅️ "),
    INLINE_CHECK("☑️️ "),
    CANCEL("❌️ "),
    STOP("⛔️️ "),
    EDIT_NEW("\uD83D\uDCDD️️ "),
    KORZINKA("\uD83E\uDD61️️ "),
    TRASH("\uD83D\uDDD1️️ "),
    LOCATION("\uD83D\uDCCD️️ "),
    BYCYCLE("\uD83D\uDEB4\uD83C\uDFFB\u200D♂️️️ "),
    TAXI("\uD83D\uDE95️️️ "),
    ORDER("\uD83D\uDCE8️️ "),
    MENU("\uD83E\uDDFE️️ "),
    FAX("\uD83D\uDCE0️️ "),
    SET("\uD83C\uDF71️️ "),
    THANKS("\uD83D\uDE4F️️ "),
    CONFIRM("\uD83D\uDD16️️ "),
    DOWN("\uD83D\uDC47  "),
    RESPECT("\uD83D\uDE4F  "),
    REPORT("\uD83D\uDCCA  "),
    Z_REPORT("\uD83E\uDDFE  "),
    CASHIER("\uD83D\uDC69\uD83C\uDFFB\u200D\uD83D\uDCBC  "),
    SHIFT("\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBB  "),
    RED_APPLE("\uD83C\uDF4E  "),


    RATE_1("\uD83D\uDC79  "),
    RATE_2("\uD83D\uDE21  "),
    RATE_3("\uD83D\uDE15  "),
    RATE_4("\uD83D\uDC4D  "),
    RATE_5("❤️  ");

    private final String unicode;
}
