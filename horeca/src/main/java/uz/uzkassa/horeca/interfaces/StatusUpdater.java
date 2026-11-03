package uz.uzkassa.horeca.interfaces;


public interface StatusUpdater<ID, Status> {
    void updateStatus(ID id, Status status);
}
