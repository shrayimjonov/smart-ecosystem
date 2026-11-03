package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import java.util.List;

public interface BaseService<T, ID> {

    @Deprecated
    List<T> findAll();

    Page<T> findAll(Pageable pageable);

    Page<T> findAll(@Nullable Specification<T> var1, Pageable var2);

    T save(T entity);

    void saveAll(Iterable<T> coll);

    T saveAndFlash(T entity);

    T findById(ID id);

    void delete(T entity);

    void deleteById(ID id);

    void deleteById(ID id, boolean check);

    long count();

    void validateEntityReferences(ID id);
}
