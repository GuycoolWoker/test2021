<%@ page contentType="text/html;charset=UTF-8" language="java" %>
// 插入jstl插件
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<html>
<head>
    <base href="<%=basePath%>">
    <title>Title</title>
</head>
<body>

    // 使用jstl对作用域中的List对象进行遍历输出
    <option></option>
    <c:forEach items="${applicationScope.clueStateList}" var="c">
        <option value="${c.value}">${c.text}</option>
    </c:forEach>

    $.ajax({
        url:"",
        data:{},
        type:"",
        dataType:"json",
        success:function(data){}
    })


    <script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
    <script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
    <script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>
    // 条件日历组件 设置日期参数以什么格式存在
    $(".time").datetimepicker({
    minView: "month",
    language:  'zh-CN',
    format: 'yyyy-mm-dd',
    autoclose: true,
    todayBtn: true,
    pickerPosition: "bottom-left"
    });



    // 创建时间: 当前系统时间
    String createTime = DateTimeUtil.getSysTime();
    // 创建人: 当前登录用户
    String createBy = ((User) req.getSession().getAttribute("user")).getName();
</body>
</html>
