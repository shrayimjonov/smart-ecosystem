package uz.uzkassa.cts.domain.apay;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.cts.domain.base.LocalizedEntity;

import java.io.Serial;


@Entity
@Table(name = "apay_category")
@Getter
@Setter
@SQLDelete(sql = "UPDATE apay_category SET deleted = 'true' WHERE id=?")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class APayCategory extends LocalizedEntity {

    @Serial
    private static final long serialVersionUID = 334347543L;

    @Column(name = "apay_id", unique = true, nullable = false)
    String apayId;

    @Column(name = "parent_id")
    String parentId;
}
