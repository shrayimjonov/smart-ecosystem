package uz.uzkassa.smartpos.domain.postgres.common;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpos.domain.postgres.base.SimpleEntity;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "application_route", uniqueConstraints = {
        @UniqueConstraint(columnNames = "annotation_name", name = "application_route_annotation_name_un_idx")
})
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApplicationRoute extends SimpleEntity {

    @Column(name = "end_point")
    String endPoint;

    @Column(name = "menu_name")
    String menuName;

    @Column(name = "annotation_name")
    String annotationName;

    Integer position;

    @Column(name = "section", columnDefinition = "boolean default false")
    boolean section = false;

    @Column(name = "parent_Id")
    Long parentId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id", updatable = false, insertable = false, foreignKey = @ForeignKey(name = "application_route_parent_fk"))
    ApplicationRoute parent;

    @OneToMany(mappedBy = "parent", orphanRemoval = true, fetch = FetchType.LAZY)
    Set<ApplicationRoute> children = new HashSet<>();
}
