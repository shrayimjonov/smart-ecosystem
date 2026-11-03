package uz.uzkassa.smartpos.repository.postgres.common;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.domain.postgres.common.ApplicationRoute;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ApplicationRouteRepository extends JpaRepository<ApplicationRoute, Long> {

    List<ApplicationRoute> findByParentIdOrderByPositionAsc(Long parentId);

    Set<ApplicationRoute> findByParentIdOrderByPosition(Long parentId);

    Optional<ApplicationRoute> findByAnnotationName(String annotationName);

    Optional<ApplicationRoute> findByAnnotationNameAndIdNot(String annotationName, Long id);
}
