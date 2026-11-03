package uz.uzkassa.smartpossupply.domain.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 2L;

    @Column(name = "deleted", columnDefinition = "boolean default false")
    protected boolean deleted = false;
}
