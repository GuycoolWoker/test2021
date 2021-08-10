package com.bjpowernode.settings.service.impl;

import com.bjpowernode.settings.dao.DicTypeDao;
import com.bjpowernode.settings.dao.DicValueDao;
import com.bjpowernode.settings.dao.UserDao;
import com.bjpowernode.settings.domain.DicType;
import com.bjpowernode.settings.domain.DicValue;
import com.bjpowernode.settings.service.DicService;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("dicService")
public class DicServiceImpl implements DicService {

    public Map<String, List<DicValue>> getAll(ServletContext application) {

        DicTypeDao dicTypeDao = WebApplicationContextUtils.getWebApplicationContext(application).getBean(DicTypeDao.class);
        DicValueDao dicValueDao = WebApplicationContextUtils.getWebApplicationContext(application).getBean(DicValueDao.class);
        UserDao userDao = WebApplicationContextUtils.getWebApplicationContext(application).getBean(UserDao.class);


        Map<String,List<DicValue>> map = new HashMap<String, List<DicValue>>();
        System.out.println("userDao="+userDao);
        System.out.println("dicTypeDao="+dicTypeDao);
        // 将字典类型列表取出
        List<DicType> dtList = dicTypeDao.getTypeList();

        // 将字典类型列表遍历
        for(DicType dicType : dtList){

            // 取得每一种类型的字典类型编码
            String code = dicType.getCode();

            // 根据每一个字典类型来取得字典值列表
            List<DicValue> dvList = dicValueDao.getListByCode(code);

            map.put(code+"List",dvList);
        }

        return map;
    }

}
