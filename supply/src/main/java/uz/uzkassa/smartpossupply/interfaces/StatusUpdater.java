package uz.uzkassa.smartpossupply.interfaces;


public interface StatusUpdater<ID, Status> {
    void updateStatus(ID id, Status status);
}
