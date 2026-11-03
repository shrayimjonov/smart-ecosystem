package uz.uzkassa.cts.dto.apay;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ApayTariffDTO {
    private boolean agent;
    private int agentFeeForTicket;
    private int cashbackMerchant;
    private int cashbackUser;
    private int commission;
    private String companyId;
    private boolean complexCalculation;
    private Date createDate;
    private String description;
    private String descriptionCyrillic;
    private String descriptionRu;
    private String descriptionUz;
    private boolean editable;
    private String id;
    private boolean isPlatinum;
    private String name;
    private String nameCyrillic;
    private String nameRu;
    private String nameUz;
    private int orderNum;
    private boolean platinum;
    private boolean premium;
    private String status;
    private String type;
    private String userId;
}
