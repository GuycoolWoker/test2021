package com.bjpowernode.settings.web.controller;

import com.bjpowernode.exception.LoginException;
import com.bjpowernode.settings.domain.User;
import com.bjpowernode.settings.service.UserService;
import com.bjpowernode.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/settings/user")
public class UserController {

    @Autowired
    @Qualifier("userService")
    private UserService us;

    public void setUs(UserService us) {
        this.us = us;
    }

    @RequestMapping(value = "/login.do",produces ="application/json")
    @ResponseBody
    protected Map<String,Object> service(HttpServletRequest request,User user) throws LoginException {

        System.out.println("进入到用户登陆的控制器");

        System.out.println("进入到验证登录操作");
        String loginAct = user.getLoginAct();
        String loginPwd = user.getLoginPwd();
        // 将密码的明文形式转换为MD5的密文形式
        loginPwd = MD5Util.getMD5(loginPwd);
        // 接收ip地址
        String ip = request.getRemoteAddr();
        System.out.println("----ip:" + ip);
        Map<String,Object> map = new HashMap<>();
        // 未来的业务层开发,统一使用代理类形态的接口对象
        user = us.login(loginAct, loginPwd, ip);

        map.put("success",true);
        request.getSession().setAttribute("user",user);
        return map;
    }

    @RequestMapping("/register.do")
    public void register() {
        System.out.println("进入到用户注册的控制器");
    }
}
