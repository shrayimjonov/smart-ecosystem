package uz.uzkassa.smartpossupply.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;


public interface CrudRestAdvanced<ID, DTO, DetailDTO, ListDTO, Filter, Status, LookUpDTO>
    extends CrudRest<ID, DTO, DetailDTO, ListDTO, Filter>, StatusUpdaterRest<ID, Status>, LookUpRest<LookUpDTO, Filter> {
    ResponseEntity<ID> create(DTO dto);

    ResponseEntity<ID> update(ID id, DTO dto);

    ResponseEntity<DetailDTO> get(ID id);

    ResponseEntity<Page<ListDTO>> getList(Filter f);

    ResponseEntity<Void> delete(ID id);

    ResponseEntity<List<LookUpDTO>> lookUp(Filter filter);

    ResponseEntity<Void> updateStatus(ID id, Status status);
}
