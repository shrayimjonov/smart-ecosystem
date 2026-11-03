package uz.uzkassa.smartpossupply.domain.planroute;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.LocationStatus;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 31.01.2023 15:29
 */
@Entity
@Table(name = "agent_tracking", indexes = {
        @Index(columnList = "agent_id", name = "agent_tracking_agent_id_idx")
})
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AgentTracking extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 5432L;

    @Column(name = "agent_id")
    Long agentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", insertable = false, updatable = false)
    User agent;

    @Column(name = "created_date")
    LocalDateTime createdDate;

    @Column(name = "longitude")
    Double longitude;

    @Column(name = "latitude")
    Double latitude;

    @Column(name = "address", length = 500)
    String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    LocationStatus status;

    @Column(name = "entity_id")
    Long entityId;

    @Column(name = "entity_type")
    @Enumerated(EnumType.STRING)
    EntityTypeEnum entityType;}