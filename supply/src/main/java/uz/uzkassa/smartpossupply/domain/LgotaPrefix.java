package uz.uzkassa.smartpossupply.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.smartpossupply.domain.base.BaseEntity;
import uz.uzkassa.smartpossupply.dto.lgota.LgotaPrefixDTO;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "lgota_prefix")
@SQLDelete(sql = "UPDATE lgota_prefix SET deleted = true WHERE id=?")
@SQLRestriction(value = "deleted = false")
public class LgotaPrefix extends BaseEntity {

    @Column(name = "prefix")
    String prefix;

    @Column(name = "name")
    String name;

    public LgotaPrefixDTO toDTO(){
        return new LgotaPrefixDTO(getPrefix(), getName());
    }
}
