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
<meta name="viewport"
	content="width=device-width, initial-scale=1, maximum-scale=1, minimal-ui, user-scalable=no">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet"
	href="<%=basePath%>js/datetimepicker/jquery.datetimepicker.css">

<script src="<%=basePath%>js/jquery/jquery.js"></script>
<script src="<%=basePath%>js/datetimepicker/jquery.datetimepicker.js"></script>
<script src="<%=basePath%>js/weixin/jweixin-1.0.0.js"></script>

<!-- amazeui -->
<meta name="renderer" content="webkit">
<meta http-equiv="Cache-Control" content="no-siteapp" />
<meta name="mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta name="apple-mobile-web-app-title" content="Amaze UI" />
<meta name="msapplication-TileImage"
	content="<%=basePath%>js/amazeui/assets/i/app-icon72x72@2x.png">
<meta name="msapplication-TileColor" content="#0e90d2">

<script src="<%=basePath%>js/amazeui/assets/js/amazeui.min.js"></script>

<link rel="icon" type="image/png"
	href="<%=basePath%>js/amazeui/assets/i/favicon.png">
<link rel="apple-touch-icon-precomposed"
	href="<%=basePath%>js/amazeui/assets/i/app-icon72x72@2x.png">
<link rel="stylesheet"
	href="<%=basePath%>js/amazeui/assets/css/amazeui.min.css">
<link rel="stylesheet" href="<%=basePath%>js/amazeui/assets/css/app.css">

<style type="text/css">
* {
	margin: 0;
	padding: 0;
	border: 0;
}

body {
	font-family: "Segoe UI", "Lucida Grande", Helvetica, Arial,
		"Microsoft YaHei", FreeSans, Arimo, "Droid Sans",
		"wenquanyi micro hei", "Hiragino Sans GB", "Hiragino Sans GB W3",
		Arial, sans-serif;
}

*, *:before, *:after {
	-moz-box-sizing: border-box;
	-webkit-box-sizing: border-box;
	box-sizing: border-box;
}

.am-topbar{
	margin-bottom: 0;
}
</style>

<title>我的行程</title>

<script type="text/javascript">
var mymatch;
	$(document).ready(function() {
		getMyMatchs();
	});
	
	function getMyMatchs() {
		$.AMUI.progress.start();
		var path = "<%=basePath%>getMyMatchs";
		var data = {};
		$.ajax({
			type : 'POST',
			data : data,
			url : path,
			success : function(result) {
				$.AMUI.progress.done();
				if(result.result == "ok"){
					obj = result.data;
					$.each(result.data, function(n, value) {
						var from = value.fromDistrict.name;
						var to = value.toDistrict.name;
						var mobile = "";
						if(value.user != null){
							mobile = value.user.mobile;
						}else{
							mobile = value.temp_user.mobile;
						}
						$("#mymatchs").append('<tr><td>'+value.time+'</td><td>'+from+'→'+to+'</td><td>'+mobile+'</td></tr>');
					});
					mymatch = result.my;
					var role = "";
					if(mymatch.type == 0){
						role = "人找车";//我是人找车
					}else{
						role = "车找人";
					}
					$("#mymatch").append("<font size='2em'>【"+role+"】"+mymatch.time+",从"+mymatch.fromDistrict.city.name+" "+mymatch.fromDistrict.name + " " + mymatch.fromAddress +" 到 "+mymatch.toDistrict.city.name+" "+mymatch.toDistrict.name+ " " + mymatch.toAddress+"</font>");
					$("#submit").attr("onclick","removeMyMatch("+mymatch.id+");");
					
					getDimMatchs();
				} else {
					alert("还没发布过出行信息");
					wx.closeWindow();
				}
				},
				dataType : "json"
			});
	}
	
	function getDimMatchs(){
		$.AMUI.progress.start();
		var path = "<%=basePath%>dimMatch";
		var data = {};
		$.ajax({
			type : 'POST',
			data : data,
			url : path,
			success : function(result) {
				$.AMUI.progress.done();
				if(result.result == "ok"){
					obj = result.data;
					if(obj.size == 0){
						$("#dimtitle").html("暂无");
					}
					var type = "";
					if(mymatch.type==0){
						type = "车找人";
					}else{
						type = "人找车";
					}
					$("#dimtitle").html("以下为<font color='red'>"+type+"</font>信息");
					$.each(result.data, function(n, value) {
						var from = value.fromDistrict.name;
						var to = value.toDistrict.name;
						var mobile = "";
						if(value.user != null){
							mobile = value.user.mobile;
						}else{
							mobile = value.temp_user.mobile;
						}
						$("#dimmatchs").append('<tr><td>'+value.time+'</td><td>'+from+'→'+to+'</td><td>'+mobile+'</td></tr>');
					});
				} else {
					$("#dimtitle").html("暂无");
				}
				},
				dataType : "json"
			});
	}
	
	function removeMyMatch(id){
		var $btn = $("#submit");
		$btn.button('loading');
		var path = "<%=basePath%>removeMatchById";
			var data = {"id":id};
			$.ajax({
				type : 'POST',
				data : data,
				url : path,
				success : function(result) {
					$btn.button('reset');
					if(result.result == "ok"){
						alert("行程已取消");
						$("#mymatch").html("");
						wx.closeWindow();
					}
				},
				dataType : "json"
			});
		}
</script>

</head>
<body>

	<div>
		<header class="am-topbar">
		<h1 class="am-topbar-brand">
			<a href="#">我的行程</a>
		</h1>
		</header>
		<a id="mymatch" style="display: block;margin: 10px 10px 10px 10px;"></a>
		<button id="submit" type="button" class="am-btn am-btn-secondary am-radius" style="margin: 10px 10px 10px 10px;">关闭行程</button>

		<header class="am-topbar">
		<h1 class="am-topbar-brand">
			<a href="#">优选伙伴</a>
		</h1>
		</header>
		<table id="mymatchs" class="am-table am-table-bordered am-table-striped">
			<tr>
				<td>出发时间</td>
				<td>路线</td>
				<td>联系方式</td>
			</tr>
		</table>

		<header class="am-topbar">
		<h1 class="am-topbar-brand">
			<a href="#">备选伙伴<a style="font-size: 12px;" id="dimtitle"></a></a>
		</h1>
		</header>
		<table id="dimmatchs" class="am-table am-table-bordered am-table-striped">
			<tr>
				<td>出发时间</td>
				<td>路线</td>
				<td>联系方式</td>
			</tr>
		</table>

	</div>

</body>
</html>