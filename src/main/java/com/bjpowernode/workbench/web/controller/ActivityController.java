package com.bjpowernode.workbench.web.controller;

import com.bjpowernode.settings.domain.User;
import com.bjpowernode.settings.service.UserService;
import com.bjpowernode.settings.service.impl.UserServiceImpl;
import com.bjpowernode.utils.DateTimeUtil;
import com.bjpowernode.utils.PrintJson;
import com.bjpowernode.utils.ServiceFactory;
import com.bjpowernode.utils.UUIDUtil;
import com.bjpowernode.vo.PaginationVo;
import com.bjpowernode.workbench.domain.Activity;
import com.bjpowernode.workbench.domain.ActivityRemark;
import com.bjpowernode.workbench.service.ActivityService;
import com.bjpowernode.workbench.service.impl.ActivityServiceImpl;
import org.hibernate.validator.constraints.pl.REGON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/workbench/activity")
public class ActivityController {

    @Autowired
    @Qualifier("activityService")
    private ActivityService as;

    @Autowired
    @Qualifier("userService")
    private UserService us;

    public void setAs(ActivityService as) {
        this.as = as;
    }

    public void setUs(UserService us) {
        this.us = us;
    }

    @RequestMapping("/updateRemark.do")
    @ResponseBody
    private Map<String,Object> updateRemark(HttpSession session,ActivityRemark ar) {
        System.out.println("进入到市场活动控制器");
        System.out.println("执行修改备注的操作");

        String editTime = DateTimeUtil.getSysTime();
        String editBy = ((User) session.getAttribute("user")).getName();
        String editFlag = "1";

        ar.setEditTime(editTime);
        ar.setEditBy(editBy);
        ar.setEditFlag(editFlag);

        boolean flag = as.updateRemark(ar);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("success", flag);
        map.put("ar", ar);

        return map;
    }

    @RequestMapping("/saveRemark.do")
    @ResponseBody
    private Map<String,Object> saveRemark(HttpSession session,ActivityRemark ar) {

        System.out.println("进入到市场活动控制器");
        System.out.println("执行添加备注操作");

        String id = UUIDUtil.getUUID();
        String createTime = DateTimeUtil.getSysTime();
        String createBy = ((User) session.getAttribute("user")).getName();
        String editFlag = "0";

        ar.setId(id);
        ar.setCreateBy(createBy);
        ar.setCreateTime(createTime);
        ar.setEditFlag(editFlag);

        boolean flag = as.saveRemark(ar);

        /**
         * data
         *      {"success":true/false,"ar":{备注}}
         */
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("success", flag);
        map.put("ar", ar);

        return map;
    }

    @RequestMapping("/deleteRemarkById.do")
    @ResponseBody
    private boolean deleteRemarkById(String id) {

        System.out.println("进入到市场活动控制器");
        System.out.println("删除备注操作");

        boolean flag = as.deleteRemarkById(id);

        return flag;
    }

    @RequestMapping("/getRemarkListByAid.do")
    @ResponseBody
    private List<ActivityRemark> getRemarkListByAid(String activityId) {

        System.out.println("进入到市场活动控制器");
        System.out.println("根据市场活动id,取得备注信息列表");

        List<ActivityRemark> arList = as.getRemarkListByAid(activityId);

        return arList;
    }

    @RequestMapping("detail.do")
    private ModelAndView detail(String id){

        System.out.println("进入到市场活动控制器");
        System.out.println("进入到跳转到详细信息页的操作");

        Activity activity = as.detail(id);

        ModelAndView mv = new ModelAndView();
        mv.addObject(activity);
        mv.setViewName("forward:/workbench/activity/detail.jsp");

        return mv;
    }

    @RequestMapping("/update.do")
    @ResponseBody
    private boolean update(HttpSession session,Activity a) {

        System.out.println("进入到市场活动控制器");
        System.out.println("执行市场活动修改操作");
        // 修改时间: 当前系统时间
        String editTime = DateTimeUtil.getSysTime();
        // 修改人: 当前登录用户
        String editBy = ((User) session.getAttribute("user")).getName();

        a.setEditTime(editTime);
        a.setEditBy(editBy);

        boolean flag = as.update(a);

        return flag;
    }

    @RequestMapping("/getUserListAndActivity.do")
    @ResponseBody
    private Map<String,Object> getUserListAndActivity(String id) {

        System.out.println("进入到市场活动控制器");
        System.out.println("进入到查询用户信息列表和根据市场活动id查询单条记录的操作");
        /**
         * 总结:
         *      controller调用service的方法,返回值是什么
         *      你得想一想前端要什么,就要从service层取什么
         *
         *  前端需要的,管业务层去要
         * uList
         * a
         *
         * 以上两项信息,复用率不高,我们选择使用map打包这两项信息即可
         * map
         */
        Map<String, Object> map = as.getUserListAndActivity(id);
        return map;
    }

    @RequestMapping("/delete.do")
    @ResponseBody
    private boolean delete(@RequestParam(value = "id") String[] ids) { // 前端传过来的参数是id,后端变量名称变成ids使用

        System.out.println("进入到市场活动控制器");
        System.out.println("执行市场活动的删除操作");
        boolean flag = as.delete(ids);
        return flag;
    }

    @RequestMapping("/pageList.do")
    @ResponseBody
    private PaginationVo<Activity> pageList(Activity activity, Integer pageSize, Integer pageNo) {

        System.out.println("进入到市场活动控制器");
        System.out.println("进入到查询市场活动信息列表的操作(结合条件查询+分页查询)");

        String name = activity.getName();
        String owner = activity.getOwner();
        String startDate = activity.getStartDate();
        String endDate = activity.getEndDate();
        // 计算出略过的记录数
        int skipCount = (pageNo - 1) * pageSize;

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", name);
        map.put("owner", owner);
        map.put("startDate", startDate);
        map.put("endDate", endDate);

        // 因为以下两条信息不在domain类中,所以选择使用map进行传值(<parameterType>传值不能使用vo类,<resultType>传值可以使用vo类)
        map.put("pageSize", pageSize);
        map.put("skipCount", skipCount);
        /**
         * 前端要: 市场活动信息列表
         *          查询的总条数
         *
         *          业务层拿到了以上两项信息之后,如何做返回
         *          map
         *          map.put("dataList",dataList);
         *          map.put("total",total);
         *          PrintJson map --> json
         *          {"total":100,"dataList":[{市场活动1},{2},{3}...]
         *
         *          vo
         *          PaginationVo<T>
         *              private int total;
         *              private List<T> dataList;
         *
         *          PaginationVo<Activity> vo = new PaginationVo<>();
         *          vo.setTotal(total);
         *          vo.setDataList(dataList);
         *          PrintJson vo --> json
         *          {"total":100,"dataList":[{市场活动1},{2},{3}...]}
         *
         *          将来分页查询: 每个模块都有,所以我们选择使用一个通用的vo,操作起来比较方便
         */
        PaginationVo<Activity> vo = as.pageList(map);

        // vo --> {"total":total,"dataList":[{市场活动1},{2},{3}...]}
        return vo;
    }

    @RequestMapping("/save.do")
    @ResponseBody
    private boolean save(HttpSession session, Activity activity) {

        System.out.println("进入到市场活动控制器");
        System.out.println("执行市场活动的添加操作");

        String id = UUIDUtil.getUUID();
        // 创建时间: 当前系统时间
        String createTime = DateTimeUtil.getSysTime();
        // 创建人: 当前登录用户
        String createBy = ((User) session.getAttribute("user")).getName();

        activity.setId(id);
        activity.setCreateTime(createTime);
        activity.setCreateBy(createBy);

        boolean flag = as.save(activity);

        return flag;
    }

    @RequestMapping("/getUserList.do")
    @ResponseBody
    private List<User> getUserList() {

        System.out.println("进入到市场活动控制器");
        System.out.println("取得用户信息列表");

        List<User> uList = us.getUserList();
        return uList;
    }

}
