package uz.uzkassa.cts.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.postfix.PostfixMailboxDTO;
import uz.uzkassa.cts.dto.postfix.PostfixMailboxEmailDTO;
import uz.uzkassa.cts.repository.mysql.PostfixRepository;
import uz.uzkassa.cts.service.PostfixService;

import java.util.List;

@Transactional(readOnly = true)
@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class PostfixServiceImpl implements PostfixService {

    PostfixRepository postfixRepository;

    /*@Override
    @Transactional
    public void createMailBox(PostfixMailboxDTO postfixMailboxDTO) {
        postfixRepository.createMailBox(postfixMailboxDTO);
    }

    @Override
    public void updateMailBoxUsername(PostfixMailboxEmailDTO postfixMailboxDTO) {
        postfixRepository.updateMailBoxEmail(postfixMailboxDTO);
    }

    @Override
    @Transactional
    public void updateMailBoxStatus(String username, boolean active) {
        postfixRepository.updateMailBoxStatus(username, active);
    }*/

    @Override
    public List<CommonDTO> getDomains() {
        //return postfixRepository.getDomains();//TODO POSTFIX
        return List.of(new CommonDTO(1L,"smartpos.uz"));
    }
}
