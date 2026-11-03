package uz.uzkassa.smartpos.integration.apay.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TariffService {
    private int amount;
    private int fromDay;
    private Date fromDayDate;
    private String id;
    private int orderNum;
    private String tariffId;
    private int toDay;
    private Date toDayDate;
    private TariffServiceType type;
}
