package uz.uzkassa.cts.domain.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@MappedSuperclass
@Getter
@Setter
public class BaseEntity extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 8958349L;

    @Column(name = "deleted", columnDefinition = "boolean default false")
    protected boolean deleted = false;
}
