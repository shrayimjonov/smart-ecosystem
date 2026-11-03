package uz.uzkassa.silen.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public enum StoreInType implements Serializable {
    AUTO,
    MANUAL
}
