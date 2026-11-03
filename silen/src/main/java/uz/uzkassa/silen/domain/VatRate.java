package uz.uzkassa.silen.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.VatRateDTO;

import java.math.BigDecimal;

@Entity
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "vat_rate", uniqueConstraints = {@UniqueConstraint(columnNames = {"code"})})
@Getter
@Setter
@SQLDelete(sql = "UPDATE vat_rate SET deleted = 'true' WHERE id=?")
public class VatRate extends BaseIdentityEntity {

    @Column(name = "code", length = 20)
    String code;

    @Column(name = "name", length = 20)
    String name;

    @Column(name = "amount")
    BigDecimal amount;

    public VatRateDTO toDTO() {
        VatRateDTO vatRateDTO = new VatRateDTO();
        vatRateDTO.setId(getId());
        vatRateDTO.setCode(getCode());
        vatRateDTO.setName(getName());
        vatRateDTO.setAmount(getAmount());
        return vatRateDTO;
    }
}
