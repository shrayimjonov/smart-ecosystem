package uz.uzkassa.cts.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;

import java.io.Serial;

@Entity
@Table(name = "pan_black_list", indexes = {
    @Index(columnList = "pan", name = "pan_idx")
})
@Getter
@Setter
@SQLDelete(sql = "UPDATE pan_black_list SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class PanBlackList extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 1573638186L;

    @Column(name = "pan", unique = true)
    private String pan;
}
