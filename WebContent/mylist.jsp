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
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, minimum-scale=1, user-scalable=no">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<!-- amazeui -->
<meta name="renderer" content="webkit">
<meta http-equiv="Cache-Control" content="no-siteapp" />
<meta name="mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta name="apple-mobile-web-app-title" content="Amaze UI" />
<meta name="msapplication-TileImage" content="<%=basePath%>js/amazeui/assets/i/app-icon72x72@2x.png">
<meta name="msapplication-TileColor" content="#0e90d2">
<title>我的行程</title>

<link rel="stylesheet" href="<%=basePath%>js/amazeui/assets/css/amazeui.min.css">
<link rel="stylesheet" href="<%=basePath%>js/amazeui/assets/css/app.css">
<link rel="stylesheet" href="<%=basePath%>js/jquery/jquery.mobile-1.4.5.min.css">
<link rel="stylesheet" href="<%=basePath%>js/datetimepicker/jquery.datetimepicker.css">

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
</style>

<script src="<%=basePath%>js/jquery/jquery.js"></script>
<script src="<%=basePath%>js/jquery/jquery.mobile-1.4.5.js"></script>
<script src="<%=basePath%>js/amazeui/assets/js/amazeui.min.js"></script>
<script src="<%=basePath%>js/weixin/jweixin-1.0.0.js"></script>

<script type="text/javascript">

$(document).ready(function(){
	getMyMatchs();
});

var obj;
function showPupwindow(index){
	var match = obj[index];
	$("#content").html("请立即与"+match.user.name+"联系，电话:"+match.user.mobile);
	$("#call").attr("href","tel:"+match.user.mobile);
	$("#clicked").trigger("click");
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
					if(obj.size == 0){
						wx.closeWindow();
					}
					$.each(result.data, function(n, value) {
						var str = "<li class='ui-li-has-alt ui-li-has-thumb ui-first-child'><a href='#' class='ui-btn'>";
						if(value.type == 0){
							//如果对方是是人找车信息，显示人形图标
							str += "<img width='160' height='160' src='imgs/icon_people.png'>";
							str += "<h2>"+value.fromDistrict.city.name+" "+value.fromDistrict.name+"<font color='green'> →到→ </font>"+value.toDistrict.city.name+" "+value.toDistrict.name+"</h2>";
							role = "人找车";
						}else{
							//如果对方是车找人信息，显示车图标
							str += "<img width='160' height='160' src='imgs/icon_car.png'>";
							str += "<h2>"+value.car+"(<font color='red'>￥"+value.price+"</font>)&emsp;从"+value.fromDistrict.city.name+" "+value.fromDistrict.name+"<font color='green'> →到→ </font>"+value.toDistrict.city.name+" "+value.toDistrict.name+"</h2>";
						}
						str += "<p>出发时间:"+value.time+"</p></a>";
						str += "<a href='javascript:showPupwindow("+n+")' class='ui-btn ui-btn-icon-notext ui-icon-forward ui-btn-a'></a><a style='visibility: hidden;' id='clicked' href='#purchase' data-rel='popup' data-position-to='window' data-transition='pop' aria-haspopup='true' aria-owns='purchase' aria-expanded='false' class='ui-btn ui-btn-icon-notext ui-icon-forward ui-btn-a' title='Purchase album'>立即预约</a>";
						str += "</li>";
						$("#listview").append(str);
					});
					var my = result.my;
					var role = "";
					if(my.type == 0){
						role = "人找车";//我是人找车
					}else{
						role = "车找人";
					}
					$("#mymatch").append("<font size='2em'>【"+role+"】"+my.time+",从"+my.fromDistrict.city.name+" "+my.fromDistrict.name + " " + my.fromAddress +" 到 "+my.toDistrict.city.name+" "+my.toDistrict.name+ " " + my.toAddress+"</font>");
					$("#submit").attr("onclick","removeMyMatch("+my.id+");");
				} else {
					alert("还没发布过出行信息");
					wx.closeWindow();
				}
				},
				dataType : "json"
			});
	}
</script>
</head>
<body>
<table>
	<tr><td colspan="2"><font size="2em">当前行程:</font></td></tr>
	<tr><td><div id="mymatch"></div></td><td><button type="button" id="submit"
		class="am-btn am-btn-secondary btn-loading-example" onclick=""
		data-am-loading="{spinner: 'circle-o-notch', loadingText: '努力提交中', resetText: '取消行程'}">取消行程</button></td></tr>
</table>
<div data-demo-html="true" style="padding: 10px;">
	<ul id="listview" data-role="listview" data-split-icon="gear" data-split-theme="a" data-inset="true" class="ui-listview ui-listview-inset ui-corner-all ui-shadow">
	</ul>
	<div data-role="popup" id="purchase" data-theme="a" data-overlay-theme="b" class="ui-content" style="max-width:340px; padding-bottom:2em;">
		<h3>请立即联系对方确定信息</h3>
		<p id="content"></p>
			<a href="" id="call" data-rel="back" class="ui-shadow ui-btn ui-corner-all ui-btn-b ui-icon-check ui-btn-icon-left ui-btn-inline ui-mini">立即联系</a>
			<a href="#" data-rel="back" class="ui-shadow ui-btn ui-corner-all ui-btn-inline ui-mini">稍后联系</a>
		</div>
	</div>

</body>
</html>