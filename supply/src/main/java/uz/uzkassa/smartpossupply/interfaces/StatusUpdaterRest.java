package uz.uzkassa.smartpossupply.interfaces;

import org.springframework.http.ResponseEntity;


public interface StatusUpdaterRest<ID, Status> {
    ResponseEntity<Void> updateStatus(ID id, Status status);
}
