package uz.uzkassa.cts.dto.kapitallegal.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class KapitalLegalAccount {
    private String account;
    private Long aid;
    private String branch;
    private Long canpay;
    private Long ct;
    private Long dt;
    private String l_date;
    private String name;
    private String o_date;
    private Long s_in;
    private Long s_out;
    private Long state;
    private String val;
}
