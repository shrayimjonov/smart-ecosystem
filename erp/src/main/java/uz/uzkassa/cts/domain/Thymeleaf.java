package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.ThymeleafTemplateType;

import java.io.Serial;

@Entity
@Table(name = "thymeleaf")
@Getter
@Setter
@SQLDelete(sql = "UPDATE thymeleaf SET deleted = 'true' WHERE id=?")
public class Thymeleaf extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 7992424612L;

    @NotNull
    @NotEmpty
    @Column(name = "name")
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "template_type")
    private ThymeleafTemplateType templateType;

    @Column(name = "active", columnDefinition = "boolean default true")
    private boolean active = true;

}
