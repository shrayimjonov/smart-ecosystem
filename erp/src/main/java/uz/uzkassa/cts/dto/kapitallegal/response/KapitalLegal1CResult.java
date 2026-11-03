package uz.uzkassa.cts.dto.kapitallegal.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.kapitalbank.KapitalLegalDayStatus;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
public class KapitalLegal1CResult {

    /**
     * Признак окончательного отчета.
     * 0 - отчет не окончательный, т.е. операционный день не закрыт и могут быть ещё поступления.
     * 1 - окончательный результат
     */
    @JsonProperty("fin")
    private KapitalLegalDayStatus dayStatus;
    @JsonProperty("oper_day")
    private String operDay;
    @JsonProperty("saldo_in")
    private BigInteger saldoIn;
    @JsonProperty("saldo_out")
    private BigInteger saldoOut;
    @JsonProperty("total_credit")
    private BigInteger totalCredit;
    @JsonProperty("total_debit")
    private BigInteger totalDebit;
    private List<KapitalLegel1CContent> content;
}
