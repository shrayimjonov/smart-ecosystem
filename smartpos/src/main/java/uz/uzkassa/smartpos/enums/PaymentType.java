package uz.uzkassa.smartpos.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uz.uzkassa.smartpos.dto.SelectItemLocalizedDTO;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum PaymentType {
    CASH("Наличные", "Naqd", "Нақд"),
    CARD("Карточка", "Karta", "Карта"),
    UZCARD("UZCARD", "UZCARD", "UZCARD"),
    HUMO("HUMO", "HUMO", "HUMO"),
    APAY("A-PAY", "APAY", "APAY"),
    APAY_WALLET("Электронный кошелек A-PAY", "APAY elektron hamyoni", "АПАЙ электрон ҳамёни"),
    PAYME("Payme", "Payme", "Payme"),
    LOYALTY_CARD("Карта лояльности", "Sodiqlik kartasi", "Содиқлик картаси"),
    NDS("НДС", "QQS", "ҚҚС"),
    DISCOUNT("Скидки", "Chegirma", "Чегирма"),
    EXCISE("Акциз", "Aksiz", "Акциз"),
    CLICK("Click", "Click", "Click"),
    UZUM("Uzum", "Uzum", "Uzum"),
    PAYNET("Paynet", "Paynet", "Paynet"),
    PAYGINE("Paygine", "Paygine", "Paygine"),
    EPAYMENT("Электронный платеж", "Elektron to'lov", "Электрон тўлов"),
    CHANGE("Сдача", "Qaytim", "Қайтим"),
    ANORBANK("Anorbank", "Anorbank", "Anorbank"),
    OSON("Oson", "Oson", "Oson"),
    OTHER("Другой", "Boshqa terminal", "Бошқа терминал"),
    ALIF("ALIF", "Alif", "Alif"),
    VISA("VISA", "Visa", "Visa"),
    SELLO("SELLO", "SELLO", "SELLO");

    private final String nameRu;
    private final String nameUz;
    private final String nameCyrillic;

    public static List<SelectItemLocalizedDTO> getList() {
        List<SelectItemLocalizedDTO> result = new ArrayList<>();
        for (PaymentType type : values()) {
            result.add(type.toSelectItem());
        }
        return result;
    }

    public SelectItemLocalizedDTO toSelectItem() {
        return new SelectItemLocalizedDTO(null, name(), getNameRu(), getNameUz(), getNameCyrillic(), name());
    }

    public static List<PaymentType> m1Types() {
        return List.of(UZCARD, HUMO, APAY, APAY_WALLET, PAYME, CLICK, UZUM, PAYNET, PAYGINE, ANORBANK, OSON, ALIF, VISA, SELLO);
    }
}
