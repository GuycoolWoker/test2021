package com.bjpowernode.workbench.dao;


import com.bjpowernode.workbench.domain.Clue;

import java.util.List;
import java.util.Map;

public interface ClueDao {


    int save(Clue clue);

    Clue detail(String id);

    Clue getById(String clueId);

    int delete(String clueId);

    List<Clue> pageList(Map<String, Object> map);

    Integer getTotalByCondition(Map<String, Object> map);

    Clue getClueById(String id);

    int updateClue(Clue clue);
}
