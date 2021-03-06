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
        System.out.println("????????????????????????");

        System.out.println("????????????????????????");

        // ???????????????????????????????????????
        String flag = req.getParameter("flag");
        String clueId = req.getParameter("clueId");
        String createBy = ((User)req.getSession().getAttribute("user")).getName();
        Tran t = null;

        if("a".equals(flag)){ // ???????????????,?????????????????????????????????,???????????????equals()?????????!!
            System.out.println("????????????????????????");

            // ??????????????????????????????
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
         * ???????????????????????????
         *  1. ?????????????????????clueId,????????????clueId??????,????????????????????????????????????
         *  2. ?????????????????????t,?????????????????????????????????,????????????????????????????????????(??????????????????t??????????????????null)
         */
        boolean flag1 = cs.convert(clueId,t,createBy);

        if(flag1){
            // ?????????
            resp.sendRedirect(req.getContextPath()+"/workbench/clue/index.jsp");
        }

    }

    @RequestMapping("/getActivityListByName.do")
    private void getActivityListByName(HttpServletRequest req, HttpServletResponse resp) {
        System.out.println("????????????????????????");

        System.out.println("????????????????????????(????????????????????????)");;

        String aname = req.getParameter("aname");

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        List<Activity> aList = as.getActivityListByName(aname);

        PrintJson.printJsonObj(resp,aList);
    }

    @RequestMapping("/bund.do")
    private void bund(HttpServletRequest req, HttpServletResponse resp) {
        System.out.println("????????????????????????");

        System.out.println("");

        String cid = req.getParameter("cid");
        String[] aids = req.getParameterValues("aid");

        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        boolean flag = cs.bund(cid,aids);

        PrintJson.printJsonFlag(resp,flag);
    }

    @RequestMapping("/getActivityListByNameAndNotByClueId.do")
    private void getActivityListByNameAndNotByClueId(HttpServletRequest req, HttpServletResponse resp) {
        System.out.println("????????????????????????");

        System.out.println("????????????????????????(?????????????????????+??????????????????????????????????????????)");

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
        System.out.println("????????????????????????");

        System.out.println("????????????????????????");

        String id = req.getParameter("id");

        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        boolean flag = cs.unbund(id);

        PrintJson.printJsonFlag(resp,flag);

    }

    @RequestMapping("/getActivityListByClueId.do")
    private void getActivityListByClueId(HttpServletRequest req, HttpServletResponse resp) {
        System.out.println("????????????????????????");

        System.out.println("????????????id?????????????????????????????????");

        String clueId = req.getParameter("clueId");

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        List<Activity> aList = as.getActivityListByClueId(clueId);

        PrintJson.printJsonObj(resp,aList);
    }

    @RequestMapping("/detail.do")
    private ModelAndView detail(String id){
        System.out.println("????????????????????????");

        System.out.println("?????????????????????????????????");

        Clue clue = cs.detail(id);

        ModelAndView mv = new ModelAndView();

        mv.addObject("c",clue);

        mv.setViewName("forward:/workbench/clue/detail.jsp");
        return mv;
    }

    @RequestMapping("/save.do")
    @ResponseBody
    private boolean save(HttpSession session, Clue clue) {
        System.out.println("????????????????????????");

        System.out.println("???????????????????????????");

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
        System.out.println("????????????????????????");

        System.out.println("????????????????????????");

        List<User> uList = us.getUserList();

        return uList;
    }
}