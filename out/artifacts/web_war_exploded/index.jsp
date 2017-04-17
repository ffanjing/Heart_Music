<%-- Created by IntelliJ IDEA. --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>sdsd</title>
    <script type='text/javascript' src='./Js/jquery-3.2.0.js'></script>
    <script type='text/javascript' src='./Js/jquery.particleground.js'></script>
</head>
<body>

<h2 id = "hhh" onclick = "connect()">连接数据库</h2>

<script>
    function connect() {
        $.ajax({
            type:"post",
            url:"/RecommendServlet",
            dataType:"json",
            data:{userID:"1"},
            complete:function (data) {
                var jsonData = eval("(" + data.responseText + ")");
                if (jsonData.status = "6")
                    $("#hhh").text("连接成功")
                else
                    $("#hhh").innerHTML = "连接失败";
            }
        })
    }
</script>

</body>
</html>