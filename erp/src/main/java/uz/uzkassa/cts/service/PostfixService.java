package uz.uzkassa.cts.service;

import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.postfix.PostfixMailboxDTO;
import uz.uzkassa.cts.dto.postfix.PostfixMailboxEmailDTO;

import java.util.List;

public interface PostfixService {

    /*void createMailBox(PostfixMailboxDTO postfixMailboxDTO);

    void updateMailBoxUsername(PostfixMailboxEmailDTO postfixMailboxDTO);

    void updateMailBoxStatus(String username, boolean active);*/

    List<CommonDTO> getDomains();
}
