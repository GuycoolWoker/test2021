package com.bjpowernode.workbench.service;

import com.bjpowernode.workbench.domain.Clue;
import com.bjpowernode.workbench.domain.Tran;

import java.util.List;
import java.util.Map;

public interface ClueService {
    boolean save(Clue clue);

    Clue detail(String id);

    boolean unbund(String id);

    boolean bund(String cid, String[] aids);

    boolean convert(String clueId, Tran t, String createBy);

    Map<String,Object> pageList(Map<String,Object> map);

    Map<String, Object> editClue(String id);

    int updateClue(Clue clue);

    int deleteclue(String[] id);
}
