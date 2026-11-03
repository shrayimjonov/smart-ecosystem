package uz.uzkassa.horeca.constants;

import uz.uzkassa.horeca.enums.CategoryType;
import uz.uzkassa.horeca.enums.LanguageEnum;

/**
 * Application constants.
 */
public interface Constants {
    String LOGIN_REGEX = "^(?>[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*)|(?>[_.@A-Za-z0-9-]+)$";
    String PHONE_REGEX = "^998[\\d]{9}$";
    String TIN_PINFL_REGEX = "[0-9]{9,14}";
    String TIN_REGEX = "[0-9]{9}";
    String PINFL_REGEX = "[0-9]{14}";
    String ACTIVATION_KEY_REGEX = "[\\d]{6}$";

    String INCREMENT_NUMBER = "INCREMENT_NUMBER";
    String NUMBER = "NUMBER";

    String HALL_ITEM_MESSAGE_RU = "Ожидает заказа";
    String HALL_ITEM_MESSAGE_UZ = "Buyurtmani kutmoqda";
    String HALL_ITEM_MESSAGE_CYRILLIC = "Буюртмани кутмоқда";

    String DEFAULT_PHOTO_URL_FOR_FOOD = "https://bucket.s3.amazonaws.com/8b95a1230cfe36422f8ebdc6f335d394";
    String DEFAULT_PHOTO_URL_FOR_PRODUCT = "https://bucket.s3.amazonaws.com/dcfabba6d91f7349bb63364b44b19203";
    String DEFAULT_PHOTO_URL_FOR_INGREDIENT = "https://bucket.s3.amazonaws.com/833d194bc104db26359c12c5cc3d5eda";
    String DEFAULT_PHOTO_URL_FOR_SEMI_FINISHED_PRODUCT = "https://bucket.s3.amazonaws.com/44747354fc0c693d68ea12823cc2979c";

    Integer LOW_PHOTO_HEIGHT = 64;
    Integer LOW_PHOTO_WIDTH = 64;

    Integer MEDIUM_PHOTO_HEIGHT = 148;
    Integer MEDIUM_PHOTO_WIDTH = 192;

    Integer HIGH_PHOTO_HEIGHT = 398;
    Integer HIGH_PHOTO_WIDTH = 518;

    default String getHallItemMessage(LanguageEnum languageEnum) {
        return switch (languageEnum) {
            case uz -> HALL_ITEM_MESSAGE_UZ;
            case cyrillic -> HALL_ITEM_MESSAGE_CYRILLIC;
            default -> HALL_ITEM_MESSAGE_RU;
        };
    }

    default String getDefaultPhotoUrlByCategoryType(CategoryType categoryType) {
        switch (categoryType){
            case FOOD -> {
                return DEFAULT_PHOTO_URL_FOR_FOOD;
            }
            case INGREDIENT -> {
                return DEFAULT_PHOTO_URL_FOR_INGREDIENT;
            }
            case SEMI_FINISHED_PRODUCT -> {
                return DEFAULT_PHOTO_URL_FOR_SEMI_FINISHED_PRODUCT;
            }
            default -> {
                return DEFAULT_PHOTO_URL_FOR_PRODUCT;
            }
        }
    }

}
