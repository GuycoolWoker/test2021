package com.bjpowernode.workbench.dao;

import com.bjpowernode.workbench.domain.ActivityRemark;

import java.util.List;

public interface ActivityRemarkDao {
    Integer getCountByAids(String[] ids);

    Integer deleteByAids(String[] ids);

    List<ActivityRemark> getRemarkListByAid(String activityId);

    Integer deleteRemarkById(String id);

    int saveRemark(ActivityRemark ar);

    int updateRemark(ActivityRemark ar);

}
