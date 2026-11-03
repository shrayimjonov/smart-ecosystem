package uz.uzkassa.cts.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.cts.domain.base.SimpleEntity;

import java.io.Serial;

@Entity
@Table(name = "customer_branch_scanner")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE customer_branch_scanner SET deleted = 'true' WHERE id=?")
public class CustomerBranchScanner extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 8708196592L;

    @NotNull
    @Column(name = "code", unique = true, nullable = false)
    String code;

    @Column(name = "name")
    String name;
}
