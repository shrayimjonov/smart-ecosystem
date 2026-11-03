package uz.uzkassa.horeca.domain.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;


@MappedSuperclass
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class BaseEntity extends SimpleEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 2L;

    @Column(name = "deleted", columnDefinition = "boolean default false")
    boolean deleted = false;
}
