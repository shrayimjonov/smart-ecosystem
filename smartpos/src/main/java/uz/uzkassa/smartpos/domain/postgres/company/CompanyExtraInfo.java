package uz.uzkassa.smartpos.domain.postgres.company;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@Table(name = "company_extra_infos")
@NoArgsConstructor
@AllArgsConstructor
public class CompanyExtraInfo {
    @Id
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id", insertable = false, updatable = false)
    private Company company;

    @Builder.Default
    @NotNull
    @Column(name = "excise_enabled", columnDefinition = "boolean default true")
    private boolean exciseEnabled = true;

    @Builder.Default
    private boolean markingCheckEnabled = false;
}
