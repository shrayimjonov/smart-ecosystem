package uz.uzkassa.cts.service;

import uz.uzkassa.cts.constants.MessageConstants;
import uz.uzkassa.cts.dto.apay.ApayRegisterCustomerBranchesDTO;
import uz.uzkassa.cts.dto.payload.FilterPayload;
import uz.uzkassa.cts.dto.payload.Payload;

public interface APayCustomerBranchService extends MessageConstants {

    void sendCustomerBranches(FilterPayload filter);

    void createCustomerBranches(Payload payload);

    void updateCustomerBranches(Payload payload);

    void deleteCustomerBranches(Payload payload);

    void quickRegister(ApayRegisterCustomerBranchesDTO dto);
}
