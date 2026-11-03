package uz.uzkassa.horeca.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;


public interface CrudRestAdvanced<ID, DTO, DetailDTO, ListDTO, Filter, Status, LookUpDTO>
        extends CrudRest<ID, DTO, DetailDTO, ListDTO, Filter>, StatusUpdaterRest<ID, Status>, LookUpRest<LookUpDTO, Filter> {
    ResponseEntity<ID> create(DTO dto);

    ResponseEntity<ID> update(ID id, DTO dto);

    ResponseEntity<DetailDTO> get(ID id);

    ResponseEntity<Page<ListDTO>> getList(Filter f);

    ResponseEntity<Void> delete(ID id, LocalDateTime lastModifiedDate);

    ResponseEntity<List<LookUpDTO>> lookUp(Filter filter);

    ResponseEntity<Void> updateStatus(ID id, Status status);
}
