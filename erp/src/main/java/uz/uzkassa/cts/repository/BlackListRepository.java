package uz.uzkassa.cts.repository;


import uz.uzkassa.cts.domain.BlackList;

import java.util.List;

public interface BlackListRepository extends BaseRepository<BlackList, Long> {
    List<BlackList> findByTinIn(List<String> tins);
}
