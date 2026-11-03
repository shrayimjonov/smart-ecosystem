package uz.uzkassa.smartpossupply.dto.aslbelgi.aic;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AicCodeHistory implements Serializable {
    List<AicCodeHistoryItem> history;

    AicCodeProduct product;

    String tnved;
}
