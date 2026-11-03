package uz.uzkassa.smartpossupply.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.address.Country;
import uz.uzkassa.smartpossupply.enums.Continent;

import java.util.List;

@Repository
public interface CountryRepository extends BaseRepository<Country, Long> {

    @Cacheable(cacheNames = COUNTRY_LIST_CACHE)
    List<Country> findAll();

    @Cacheable(cacheNames = COUNTRY_BY_CONTINENT_CACHE)
    List<Country> findAllByContinent(Continent continent);

}
