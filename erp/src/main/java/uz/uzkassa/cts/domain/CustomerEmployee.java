package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.Role;

import java.io.Serial;

@Entity
@Table(name = "customer_employee", indexes = {
        @Index(columnList = "customer_id", name = "customer_employee_customer_id_idx"),
        @Index(columnList = "role", name = "customer_employee_role_idx")
})
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE customer_employee SET deleted = 'true' WHERE id=?")
public class CustomerEmployee extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 9079376977L;

    @NotNull
    @Column(name = "customer_id", nullable = false)
    Long customerId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", updatable = false, insertable = false)
    Customer customer;

    @NotNull
    @Column(name = "role", nullable = false)
    @Enumerated(value = EnumType.STRING)
    Role role;

    @Size(max = 50)
    @Column(name = "first_name", length = 50)
    String firstName;

    @Size(max = 50)
    @Column(name = "last_name", length = 50)
    String lastName;

    @Size(max = 50)
    @Column(name = "patronymic", length = 50)
    String patronymic;

    @Override
    public String getName() {
        StringBuilder fullName = new StringBuilder();
        if (StringUtils.isNotEmpty(getLastName())) {
            fullName.append(getLastName().trim()).append(" ");
        }
        if (StringUtils.isNotEmpty(getFirstName())) {
            fullName.append(getFirstName()).append(" ");
        }
        if (StringUtils.isNotEmpty(getPatronymic())) {
            fullName.append(getPatronymic());
        }
        return fullName.toString().trim();
    }
}
