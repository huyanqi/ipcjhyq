<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head lang="en">
  <meta charset="UTF-8">
  <title>登录到 i拼车</title>
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
  <meta name="format-detection" content="telephone=no">
  <meta name="renderer" content="webkit">
  <meta http-equiv="Cache-Control" content="no-siteapp" />
  <link rel="stylesheet" href="<%=basePath%>js/amazeui/assets/css/amazeui.min.css"/>
  <style>
    .header {
      text-align: center;
    }
    .header h1 {
      font-size: 200%;
      color: #333;
      margin-top: 30px;
    }
    .header p {
      font-size: 14px;
    }
  </style>
</head>
<body>
<div class="header">
  <div class="am-g">
    <h1>i拼车</h1>
    <p>最潮的出行方式好么？<br/>( ゜- ゜)つロ 乾杯~</p>
  </div>
  <hr />
</div>
<div class="am-g">
  <div class="am-u-lg-6 am-u-md-8 am-u-sm-centered">
    <h3>登录</h3>
    <hr>
    <br>

    <form method="post" action="adminLogin" class="am-form" id="myform" data-am-validator >
      <label for="email">账户:</label>
      <input type="text" id="username" name="username" minlength="6" required >
      <br>
      <label for="password">密码:</label>
      <input type="password" id="password" name="password" minlength="6" required >
      <br>
      <br />
      <div class="am-cf">
        <input type="submit" name="" value="提交" class="am-btn am-btn-primary am-btn-sm am-fl">
        <input type="button" name="" value="忘记密码 ^_^? " class="am-btn am-btn-default am-btn-sm am-fr">
      </div>
    </form>
    <hr>
    <p>© 2015 <a href="#" target="_blank">i拼车.</a> Powered by Frankie.</p>
  </div>
</div>

<script src="<%=basePath%>js/amazeui/assets/js/jquery.min.js"></script>
<script src="<%=basePath%>js/amazeui/assets/js/amazeui.min.js"></script>
<script src="<%=basePath%>js/jquery.form.js"></script>

<script>
$(function() {
	$('#myform').submit(function() { //提交表单   
        var options = {
            type:'POST',  
            dataType:'json',  
            success: callBack  
        };   
        $('#myform').ajaxSubmit(options);   
        return false;    
      
    });
});

function callBack(msg,status) {
	if (msg.result == "ok") {
        location.href = msg.data;
    }else{
    	alert(msg.data);
    }
} 

function toSubmit(){
	$.AMUI.progress.start();
	
	var username = $("#username").val();
	var password = $("#password").val();
	
	var data = {"username":username,"password":password};
	var path = "<%=basePath%>adminLogin";
	$.ajax({
		type : 'POST',
		data : data,
		url : path,
		success : function(result) {
			$.AMUI.progress.done();
			if (result.result == "ok") {
				alert("ok");
			} else {
				alert(result.data);
			}
		},
		dataType : "json"
	});
}

function getCookie(name) { 
    var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");
 
    if(arr=document.cookie.match(reg))
 
        return unescape(arr[2]); 
    else 
        return null; 
} 

</script>

</body>
</html>