package uz.uzkassa.silen.repository.pg;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uz.uzkassa.silen.domain.Changelog;

public interface ChangelogRepository extends JpaRepository<Changelog, Long> {

    Page<Changelog> findAllByVersionId(Long versionId, Pageable pageable);
}
