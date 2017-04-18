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
            data:{method:"getPlayList",type:"1",userID:"1"},
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




<script>
    $(function(){

        //服务器数据
        $.ajax({
            type:"post",
            url:"/RecommendServlet",
            dataType:"json",
            data:{userName:"zy"},
            complete:function (data) {
            }
        })


        //服务器数据
        $.ajax({
            type:"post",
            url:"/MayLikeServlet",
            dataType:"json",
            data:{userName:"zy1"},
            complete:function (data) {
            }
        })




        // 获取音乐列表
        $.ajax({
            type: "GET",
            url: "/Api/Json/Music.json",
            dataType: "json",
            success: function(data){
                history_list = data;
                // console.log(history_list);

                //生成历史列表
                // for(var i in history_list){
                //     var id = "history_item_1";
                //     var $li = $("<li></li>");
                //     var $a = $("<a>");
                //     var $audio = $("<audio></audio>");
                //     var $source = $("<source>");
                //     $li.attr({"id":id, "class":"history_list", "t_name":history_list[i].name,
                //         "t_artist":history_list[i].artist, "t_cover":history_list[i].cover});
                //     $a.attr({"href":"#"}).text(history_list[i].name + " - " + history_list[i].artist);
                //     $audio.attr({"preload":"none"});
                //     $source.attr({"src":history_list[i].url,"type":"audio/mp4"});
                //     $li.append($a);
                //     $audio.append($source);
                //     $li.append($audio);
                //     $("#playlist ul").append($li);
                // }


                //生成推荐列表
                normal_rec_list_arr = random_list(10,0,history_list.length);
                rec_section(normal_rec_list_arr,$("#Section1 ul"));



                //生成猜你喜欢列表
                relative_rec_list_arr = random_list(10,0,history_list.length);
                rec_section(relative_rec_list_arr,$("#Section2 ul"));

            }
        });
    })
</script>

</body>
</html>