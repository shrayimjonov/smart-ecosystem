package uz.uzkassa.smartpossupply.dto.aslbelgi.aic;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AicCodeHistoryItem implements Serializable {

    String type;//: "AGGREGATION",
    String docId;//"docId": "5646d172-5ffb-40ff-89d5-03e7b1fdd251",
    String documentType;//"documentType": "AIC_AGGREGATION",
    String documentStatus;//"documentStatus": "CHECKED_OK",
}
