package uz.uzkassa.smartpos.domain.postgres;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.base.audit.AbstractAuditingEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Reference extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Column(unique = true)
    private String code;
    @NotNull
    private String nameRu;
    @NotNull
    private String nameUz;
    @NotNull
    private String nameCyrillic;
    @Column(name = "parent_id")
    private Long parentId;
    @ManyToOne
    @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    private Reference parent;
    @OneToMany(mappedBy = "parent",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reference> children = new ArrayList<>();
    private String value;
}
