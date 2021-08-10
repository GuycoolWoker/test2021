package com.bjpowernode.workbench.web.controller;

import com.bjpowernode.settings.domain.User;
import com.bjpowernode.settings.service.UserService;
import com.bjpowernode.settings.service.impl.UserServiceImpl;
import com.bjpowernode.utils.DateTimeUtil;
import com.bjpowernode.utils.PrintJson;
import com.bjpowernode.utils.ServiceFactory;
import com.bjpowernode.utils.UUIDUtil;
import com.bjpowernode.vo.PaginationVo;
import com.bjpowernode.workbench.domain.*;
import com.bjpowernode.workbench.service.ActivityService;
import com.bjpowernode.workbench.service.CustomerService;
import com.bjpowernode.workbench.service.TranService;
import com.bjpowernode.workbench.service.impl.ActivityServiceImpl;
import com.bjpowernode.workbench.service.impl.CustomerServiceImpl;
import com.bjpowernode.workbench.service.impl.TranServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.PersistenceDelegate;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        System.out.println("进入到交易控制器");

        String path = req.getServletPath();

        if("/workbench/transaction/add.do".equals(path)){

            add(req,resp);

        }else if("/workbench/transaction/getCustomerName.do".equals(path)){

            getCustomerName(req,resp);

        }else if("/workbench/transaction/save.do".equals(path)){

            save(req,resp);

        }else if("/workbench/transaction/detail.do".equals(path)){

            detail(req, resp);

        }else if("/workbench/transaction/getHistoryListByTranId.do".equals(path)){

            getHistoryListByTranId(req,resp);

        }else if("/workbench/transaction/changeStage.do".equals(path)){

            changeStage(req,resp);

        }else if("/workbench/transaction/getChars.do".equals(path)){

            getChars(req,resp);

        }
    }

    private void getChars(HttpServletRequest req, HttpServletResponse resp) {

        System.out.println("取得交易阶段数量统计图标的数据");

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        /**
         * 业务层为我们返回
         *  total
         *  dataList
         *
         *  通过map打包以上两项进行返回
         */
        Map<String,Object> map = ts.getChars();

        PrintJson.printJsonObj(resp,map);

    }

    private void changeStage(HttpServletRequest req, HttpServletResponse resp) {

        System.out.println("执行改变阶段的操作");

        String id = req.getParameter("id");
        String stage = req.getParameter("stage");
        String money = req.getParameter("money");
        String expectedDate = req.getParameter("expectedDate");
        String editTime = DateTimeUtil.getSysTime();
        String editBy = ((User)req.getSession().getAttribute("user")).getName();

        Tran t = new Tran();
        t.setId(id);
        t.setStage(stage);
        t.setMoney(money);
        t.setExpectedDate(expectedDate);
        t.setEditBy(editBy);
        t.setEditTime(editTime);

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        boolean flag = ts.changeStage(t);

        Map<String,String> pMap = (Map<String, String>) req.getServletContext().getAttribute("pMap");
        String possibility = pMap.get(stage);

        t.setPossibility(possibility);

        Map<String,Object> map = new HashMap<String, Object>();
        map.put("success",flag);
        map.put("t",t);

        PrintJson.printJsonObj(resp,map);

    }

    private void getHistoryListByTranId(HttpServletRequest req, HttpServletResponse resp) {

        System.out.println("根据交易id取得相应的历史列表");

        String tranId = req.getParameter("tranId");

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        List<TranHistory> ths = ts.getHistoryListByTranId(tranId);

        // 阶段和可能性之间的对应关系
        Map<String,String> pMap = (Map<String, String>) req.getServletContext().getAttribute("pMap");

        // 将交易历史列表遍历
        for(TranHistory th : ths){

            // 根据每条交易历史,取出每一个阶段
            String stage = th.getStage();
            String possibility = pMap.get(stage);
            th.setPossibility(possibility);

        }

        PrintJson.printJsonObj(resp,ths);

    }

    private void detail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        System.out.println("跳转到详细信息页");

        String id = req.getParameter("id");

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        Tran t = ts.detail(id);

        // 处理可能性
        /**
         * 阶段t
         * 阶段和可能性之间的对应关系 pMap
         */

        String stage = t.getStage();
        Map<String,String> pMap = (Map<String, String>) req.getServletContext().getAttribute("pMap");
        String possibility = pMap.get(stage);
        t.setPossibility(possibility);

        req.setAttribute("t",t);

        req.getRequestDispatcher("/workbench/transaction/detail.jsp").forward(req,resp);

    }

    private void save(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        System.out.println("执行添加交易的操作");

        String id = UUIDUtil.getUUID();
        String owner = req.getParameter("owner");
        String money = req.getParameter("money");
        String name = req.getParameter("name");
        String expectedDate = req.getParameter("expectedDate");
        String customerName = req.getParameter("customerName"); // 此处我们暂时只有客户名称,还没有id
        String stage = req.getParameter("stage");
        String type = req.getParameter("type");
        String source = req.getParameter("source");
        String activityId = req.getParameter("activityId");
        String contactsId = req.getParameter("contactsId");
        String createBy = ((User)req.getSession().getAttribute("user")).getName();
        String createTime = DateTimeUtil.getSysTime();
        String description = req.getParameter("description");
        String contactSummary = req.getParameter("contactSummary");
        String nextContactTime = req.getParameter("nextContactTime");

        Tran t = new Tran();
        t.setType(type);
        t.setName(name);
        t.setId(id);
        t.setDescription(description);
        t.setMoney(money);
        t.setExpectedDate(expectedDate);
        t.setStage(stage);
        t.setActivityId(activityId);
        t.setCreateTime(createTime);
        t.setCreateBy(createBy);
        t.setSource(source);
        t.setOwner(owner);
        t.setNextContactTime(nextContactTime);
        t.setContactSummary(contactSummary);
        t.setContactsId(contactsId);

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        boolean flag = ts.save(t,customerName);

        if(flag){

            // 如果添加交易成功,跳转到列表页
            resp.sendRedirect(req.getContextPath()+"/workbench/transaction/index.jsp");

        }

    }

    private void getCustomerName(HttpServletRequest req, HttpServletResponse resp) {

        System.out.println("取得客户名称列表(按照客户的名称进行模糊查询)");

        String name = req.getParameter("name");

        CustomerService cs = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());

        List<String> sList = cs.getCustomerName(name);

        PrintJson.printJsonObj(resp,sList);

    }

    private void add(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        System.out.println("进入到跳转到交易添加页的操作");

        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());

        List<User> uList = us.getUserList();

        req.setAttribute("uList",uList);

        req.getRequestDispatcher("/workbench/transaction/save.jsp").forward(req,resp);

    }
}
