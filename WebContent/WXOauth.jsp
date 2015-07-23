<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>正在努力加载...</title>

<link rel="stylesheet" href="<%=basePath%>js/amazeui/assets/css/amazeui.min.css">
<link rel="stylesheet" href="<%=basePath%>js/amazeui/assets/css/app.css">

<script src="<%=basePath%>js/jquery/jquery.js"></script>
<script src="<%=basePath%>js/amazeui/assets/js/amazeui.min.js"></script>
<script type="text/javascript">

$(document).ready(function(){
	$.AMUI.progress.start();
	var code = getUrlParam("code");
	var state = getUrlParam("state");
	if(code == ""){
		alert("微信授权失败");
	}else{
		WXOAuthCompleted(code,state);
	}
});

function WXOAuthCompleted(code, state) {
	var path = "<%=basePath%>WXOAuthCompleted";
	var data = {"code":code,"state":state};
	$.ajax({
		type : 'POST',
		data : data,
		url : path,
		success : function(result) {
			$.AMUI.progress.done();
			window.location.href = result.state+".jsp";
		},
		dataType : "json"
	});
}

function getUrlParam(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
    var r = window.location.search.substr(1).match(reg);  //匹配目标参数
    if (r != null) return unescape(r[2]); return null; //返回参数值
}

</script>
</head>
<body>
</body>
</html>