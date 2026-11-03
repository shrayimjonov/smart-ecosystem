package uz.uzkassa.horeca.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uz.uzkassa.horeca.dto.base.CommonLocalizedDTO;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum ReceiptPaymentType {
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
    ALIF("ALIF", "Alif", "Alif");

    private final String nameRu;
    private final String nameUz;
    private final String nameCyrillic;

    public static List<CommonLocalizedDTO> getList() {
        List<CommonLocalizedDTO> result = new ArrayList<>();
        for (ReceiptPaymentType type : values()) {
            if (!EnumSet.of(NDS, DISCOUNT, EXCISE).contains(type)) {
                result.add(type.toSelectItem());
            }
        }
        return result;
    }

    public static List<CommonLocalizedDTO> getListForTerminal() {
        List<CommonLocalizedDTO> result = new ArrayList<>();
        for (ReceiptPaymentType type : values()) {
            result.add(type.toSelectItem());
        }
        return result;
    }

    public CommonLocalizedDTO toSelectItem() {
        return new CommonLocalizedDTO(null, getNameUz(), getNameCyrillic(), getNameRu(), name());
    }
}
