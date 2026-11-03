package uz.uzkassa.smartpossupply.dto.lot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LotDTO implements Serializable {
    private String lotId;
    private String sellerTin;
    private String buyerTin;
    private String buyerAccount;
    private String contractNo;
    private String contractDate;
    private List<LotProductDTO> products;
}
