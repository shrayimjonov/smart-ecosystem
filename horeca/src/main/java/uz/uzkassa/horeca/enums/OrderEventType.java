package uz.uzkassa.horeca.enums;

import lombok.Getter;

import java.io.Serializable;

/**
 * Powered by Shuxratjon Rayimjonov
 * Date: 23.05.2024
 */
@Getter
public enum OrderEventType implements Serializable {
    ORDER_CREATE,
    ORDER_UPDATE,
    ORDER_CANCELED,
    ITEM_CREATE,
    ITEM_UPDATE,
    ITEM_REMOVE,
    ITEM_READY,
    ITEM_CANCELED
}
