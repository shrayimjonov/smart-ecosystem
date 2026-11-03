package uz.uzkassa.smartpos.domain.postgres.warehouse;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.HashSet;

@Entity
@Table(name = "marking_history_item")
@Getter
@Setter
@NoArgsConstructor
public class MarkingHistoryItem implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marking_history_id")
    private MarkingHistory markingHistory;
    @NotBlank
    private String clearCode;
    @NotBlank
    private String originalCode;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private HashSet<String> children;
    private String status;
}
