package uz.uzkassa.smartpos.constants;

/**
 * Application constants.
 */
public interface Constants {

    // Regex for acceptable logins
    String LOGIN_REGEX = "^998[\\d]{9}$";

    String SYSTEM_ACCOUNT = "system";
    String DEFAULT_LANGUAGE = "ru";

    String PUBLIC_OFFER = """
            Ommaviy oferta shartnomasi: https://smartpos.uz/wp-content/uploads/2024/06/smart-trade-oferta-lat-new.pdf
            
            Publichnaya oferta: https://smartpos.uz/wp-content/uploads/2024/06/smart-trade-oferta.pdf\s""";

    //Telegram identifier
    String QUOTE = "QUOTE";

    //Attributes
    String BRANDS = "BRANDS";

    String COMMON_SMS = """
            SMART BUSINESS tizimiga kirish uchun PIN-kodni hech kimga bermang! 
            Nikomu ne peredavayte PIN-kod dlya vxoda v sistemu SMART BUSINESS.
            PIN-kod: \s""";
    String MAX_DETAIL_SIZE = "MAX_DETAIL_SIZE";
    String MAX_MARKINGS_SIZE = "MAX_MARKINGS_SIZE";
}
