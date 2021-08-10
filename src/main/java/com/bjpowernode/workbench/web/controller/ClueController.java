package com.bjpowernode.workbench.web.controller;

import com.bjpowernode.settings.domain.User;
import com.bjpowernode.settings.service.UserService;
import com.bjpowernode.utils.DateTimeUtil;
import com.bjpowernode.utils.PrintJson;
import com.bjpowernode.utils.ServiceFactory;
import com.bjpowernode.utils.UUIDUtil;
import com.bjpowernode.workbench.domain.Activity;
import com.bjpowernode.workbench.domain.Clue;
import com.bjpowernode.workbench.domain.Tran;
import com.bjpowernode.workbench.service.ActivityService;
import com.bjpowernode.workbench.service.ClueService;
import com.bjpowernode.workbench.service.impl.ActivityServiceImpl;
import com.bjpowernode.workbench.service.impl.ClueServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/workbench/clue")
public class ClueController{

    @Autowired
    @Qualifier("clueService")
    private ClueService cs;

    @Autowired
    @Qualifier("userService")
    private UserService us;



    @RequestMapping("/deleteClue.do")
    @ResponseBody
    public boolean deleteclue(@RequestParam(value = "id") String[] ids){
        boolean flag = true;
        for(String str:ids){
            System.out.println(str);
        }
        int count = ids.length;
        int result = cs.deleteclue(ids);
        if(count != result){
            flag = false;
        }
        return flag;
    }

    @RequestMapping("/updateClue.do")
    @ResponseBody
    public boolean updateClue(HttpSession session,Clue clue){
        String description = clue.getDescription();
        System.err.println(description);
        boolean flag = true;
        String editBy = ((User)session.getAttribute("user")).getName();
        String editTime = DateTimeUtil.getSysTime();
        clue.setEditBy(editBy);
        clue.setEditTime(editTime);
        int count = cs.updateClue(clue);
        if(count != 1){
            flag = false;
        }
        return flag;
    }

    @RequestMapping("/editClue.do")
    @ResponseBody
    public Map<String,Object> editClue(String id){
        Map<String,Object> map = cs.editClue(id);
        return map;
    }

    @RequestMapping("/pageList.do")
    @ResponseBody
    public Map<String,Object> pageList(Clue clue,Integer pageNo,Integer pageSize){
        System.out.println("=================================");
        String fullname = clue.getFullname();
        String company = clue.getCompany();
        String phone = clue.getPhone();
        String source = clue.getSource();
        String owner = clue.getOwner();
        String mphone = clue.getMphone();
        String state = clue.getState();

        int skipCount = (pageNo-1) * pageSize;
        Map<String,Object> map = new HashMap<>();
        map.put("skipCount",skipCount);
        map.put("pageSize",pageSize);
        map.put("fullname",fullname);
        map.put("company",company);
        map.put("phone",phone);
        map.put("source",source);
        map.put("owner",owner);
        map.put("mphone",mphone);
        map.put("state",state);
        Map<String,Object> cMap = cs.pageList(map);
        return cMap;
    }

    @RequestMapping("/convert.do")
    private void convert(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("进入到线索控制器");

        System.out.println("执行线索转换操作");

        // 接收是否需要创建交易的标记
        String flag = req.getParameter("flag");
        String clueId = req.getParameter("clueId");
        String createBy = ((User)req.getSession().getAttribute("user")).getName();
        Tran t = null;

        if("a".equals(flag)){ // 注意啊兄弟,字符串之间比较是否相等,是需要使用equals()函数的!!
            System.out.println("进入到线索控制器");

            // 接收交易表单中的参数
            String money = req.getParameter("money");
            String name = req.getParameter("name");
            String expectedDate = req.getParameter("expectedDate");
            String stage = req.getParameter("stage");
            String activityId = req.getParameter("activityId");
            String id = UUIDUtil.getUUID();
            String createTime = DateTimeUtil.getSysTime();

            t = new Tran();

            t.setId(id);
            t.setMoney(money);
            t.setName(name);
            t.setExpectedDate(expectedDate);
            t.setStage(stage);
            t.setActivityId(activityId);
            t.setCreateTime(createTime);
            t.setCreateBy(createBy);

        }

        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        /**
         * 为业务层传递的参数
         *  1. 必须传递的参数clueId,有了这个clueId之后,我们才知道要转换哪条记录
         *  2. 必须传递的参数t,因为在线索转换的过程中,有可能会临时创建一笔交易(业务层接收的t也有可能是个null)
         */
        boolean flag1 = cs.convert(clueId,t,createBy);

        if(flag1){
            // 重定向
            resp.sendRedirect(req.getContextPath()+"/workbench/clue/index.jsp");
        }

    }

    @RequestMapping("/getActivityListByName.do")
    private void getActivityListByName(HttpServletRequest req, HttpServletResponse resp) {
        System.out.println("进入到线索控制器");

        System.out.println("查询市场活动列表(根据名称模糊查询)");;

        String aname = req.getParameter("aname");

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        List<Activity> aList = as.getActivityListByName(aname);

        PrintJson.printJsonObj(resp,aList);
    }

    @RequestMapping("/bund.do")
    private void bund(HttpServletRequest req, HttpServletResponse resp) {
        System.out.println("进入到线索控制器");

        System.out.println("");

        String cid = req.getParameter("cid");
        String[] aids = req.getParameterValues("aid");

        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        boolean flag = cs.bund(cid,aids);

        PrintJson.printJsonFlag(resp,flag);
    }

    @RequestMapping("/getActivityListByNameAndNotByClueId.do")
    private void getActivityListByNameAndNotByClueId(HttpServletRequest req, HttpServletResponse resp) {
        System.out.println("进入到线索控制器");

        System.out.println("查询市场活动列表(根据名称模糊查+排除掉已经关联指定线索的列表)");

        String aname = req.getParameter("aname");
        String clueId = req.getParameter("clueId");

        Map<String,String> map = new HashMap<String, String>();
        map.put("aname",aname);
        map.put("clueId",clueId);

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        List<Activity> aList = as.getActivityListByNameAndNotByClueId(map);

        PrintJson.printJsonObj(resp,aList);
    }

    @RequestMapping("/unbund.do")
    private void unbund(HttpServletRequest req, HttpServletResponse resp) {
        System.out.println("进入到线索控制器");

        System.out.println("执行解除关联操作");

        String id = req.getParameter("id");

        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        boolean flag = cs.unbund(id);

        PrintJson.printJsonFlag(resp,flag);

    }

    @RequestMapping("/getActivityListByClueId.do")
    private void getActivityListByClueId(HttpServletRequest req, HttpServletResponse resp) {
        System.out.println("进入到线索控制器");

        System.out.println("根据线索id查询关联的市场活动列表");

        String clueId = req.getParameter("clueId");

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        List<Activity> aList = as.getActivityListByClueId(clueId);

        PrintJson.printJsonObj(resp,aList);
    }

    @RequestMapping("/detail.do")
    private ModelAndView detail(String id){
        System.out.println("进入到线索控制器");

        System.out.println("跳转到线索的详细信息页");

        Clue clue = cs.detail(id);

        ModelAndView mv = new ModelAndView();

        mv.addObject("c",clue);

        mv.setViewName("forward:/workbench/clue/detail.jsp");
        return mv;
    }

    @RequestMapping("/save.do")
    @ResponseBody
    private boolean save(HttpSession session, Clue clue) {
        System.out.println("进入到线索控制器");

        System.out.println("执行线索的添加操作");

        String id = UUIDUtil.getUUID();
        String createBy = ((User)session.getAttribute("user")).getName();
        String createTime = DateTimeUtil.getSysTime();
        clue.setId(id);
        clue.setCreateBy(createBy);
        clue.setCreateTime(createTime);

        boolean flag = cs.save(clue);

        return flag;
    }

    @RequestMapping("/getUserList.do")
    @ResponseBody
    private List<User> getUserList() {
        System.out.println("进入到线索控制器");

        System.out.println("取得用户信息列表");

        List<User> uList = us.getUserList();

        return uList;
    }
}