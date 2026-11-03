package uz.uzkassa.smartpos.domain.postgres.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Entity
@Table(name = "permission")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Permission implements Serializable {

    @Id
    @Column(name = "code", nullable = false, unique = true)
    String code;

}
