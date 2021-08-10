package com.bjpowernode.workbench.service.impl;

import com.bjpowernode.settings.dao.UserDao;
import com.bjpowernode.settings.domain.User;
import com.bjpowernode.utils.SqlSessionUtil;
import com.bjpowernode.utils.UUIDUtil;
import com.bjpowernode.vo.PaginationVo;
import com.bjpowernode.workbench.dao.ActivityDao;
import com.bjpowernode.workbench.dao.ActivityRemarkDao;
import com.bjpowernode.workbench.domain.Activity;
import com.bjpowernode.workbench.domain.ActivityRemark;
import com.bjpowernode.workbench.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("activityService")
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    @Qualifier("activityDao")
    private ActivityDao activityDao;

    @Autowired
    @Qualifier("activityRemarkDao")
    private ActivityRemarkDao activityRemarkDao;

    @Autowired
    @Qualifier("userDao")
    private UserDao userDao;

    public void setActivityDao(ActivityDao activityDao) {
        this.activityDao = activityDao;
    }

    public void setActivityRemarkDao(ActivityRemarkDao activityRemarkDao) {
        this.activityRemarkDao = activityRemarkDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public boolean save(Activity activity) {
        boolean flag = true;
        int result = activityDao.save(activity);
        if(result != 1){
            flag = false;
        }
        return flag;
    }

    public PaginationVo<Activity> pageList(Map<String, Object> map) {

        // 取得total
        Integer total = activityDao.getTotalByCondition(map);
        // 取得dataList
        List<Activity> aList = activityDao.getActivityListByCondition(map);
        // 将total和dataList封装到vo中
        PaginationVo<Activity> vo = new PaginationVo<Activity>();
        vo.setTotal(total);
        vo.setDataList(aList);

        // 将vo返回
        return vo;
    }

    public boolean delete(String[] ids) {

        boolean flag = true;
        // 查询出需要删除的备注的数量
        Integer count1 = activityRemarkDao.getCountByAids(ids);

        // 删除备注,返回受到影响的条数(实际删除的数量)
        Integer count2 = activityRemarkDao.deleteByAids(ids);

        if(count1 != count2){
            flag = false;
        }
        // 删除市场活动
        Integer count3 = activityDao.delete(ids);

        if(count3 != ids.length){
            flag = false;
        }
        return flag;
    }

    public Map<String, Object> getUserListAndActivity(String id) {

        // 取uList
        List<User> uList = userDao.getUserList();
        // 取a
        Activity a = activityDao.getById(id);
        // 打包到map集合中
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("uList",uList);
        map.put("a",a);

        return map;
    }

    public boolean update(Activity a) {
        boolean flag = true;
        int result = activityDao.update(a);
        if(result != 1){
            flag = false;
        }
        return flag;
    }


    public Activity detail(String id) {
        Activity activity = activityDao.detail(id);
        return activity;
    }

    public List<ActivityRemark> getRemarkListByAid(String activityId) {
        List<ActivityRemark> arList = activityRemarkDao.getRemarkListByAid(activityId);
        return arList;
    }

    public boolean deleteRemarkById(String id) {

        boolean flag = true;

        Integer count = activityRemarkDao.deleteRemarkById(id);

        if(count != 1){

            flag = false;

        }

        return flag;
    }

    public boolean saveRemark(ActivityRemark ar) {
        int count = activityRemarkDao.saveRemark(ar);

        boolean flag = true;

        if(count != 1){
            flag = false;
        }

        return flag;
    }

    public boolean updateRemark(ActivityRemark ar) {
        int count = activityRemarkDao.updateRemark(ar);

        boolean flag = true;

        if(count != 1){
            flag = false;
        }
        return flag;
    }

    public List<Activity> getActivityListByClueId(String clueId) {

        List<Activity> aList = activityDao.getActivityListByClueId(clueId);
        return aList;
    }

    public List<Activity> getActivityListByNameAndNotByClueId(Map<String, String> map) {

        List<Activity> aList = activityDao.getActivityListByNameAndNotByClueId(map);

        return aList;

    }

    public List<Activity> getActivityListByName(String aname) {

        List<Activity> aList = activityDao.getActivityListByName(aname);

        return aList;
    }
}
