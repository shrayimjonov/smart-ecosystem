package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import uz.uzkassa.smartpossupply.constants.CacheConstants;

@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID> , CacheConstants {}
