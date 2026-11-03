package uz.uzkassa.horeca.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;


public interface CrudRest<ID, DTO, DetailDTO, ListDTO, Filter> {
    ResponseEntity<ID> create(DTO dto);

    ResponseEntity<ID> update(ID id, DTO dto);

    ResponseEntity<DetailDTO> get(ID id);

    ResponseEntity<Page<ListDTO>> getList(Filter f);

    ResponseEntity<Void> delete(ID id, LocalDateTime lastModifiedDate);
}
