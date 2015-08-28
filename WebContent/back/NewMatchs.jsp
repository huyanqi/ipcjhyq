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
<link rel="stylesheet"
	href="<%=basePath%>js/datetimepicker/jquery.datetimepicker.css">

<script src="<%=basePath%>js/jquery/jquery.js"></script>
<script src="<%=basePath%>js/jquery/moment.js"></script>
<script src="<%=basePath%>js/datetimepicker/jquery.datetimepicker.js"></script>

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

#top_search_ly{
	list-style: none;
}
#top_search_ly li{
	float: left;
}
</style>

<title>最新匹配记录查看</title>

<script type="text/javascript">

	$(document).ready(function() {
		getSession();
	});
	
	function getSession(){
		var path = "<%=basePath%>getSession";
		$.ajax({
			type : 'POST',
			url : path,
			success : function(result) {
				if(result.data != null){
					moment.locale('zh-cn');
					getMatchs();
					getSMSs();
				}
			},
			dataType : "json"
		});
	}
	
	var pageNum = 1;
	var hasData = false;
	
	function getMatchs(){
		var path = "<%=basePath%>getMatchs";
		var data = {"pageNum":pageNum};
		$.ajax({
			type : 'POST',
			data : data,
			url : path,
			success : function(result) {
				if(result.result == "ok"){
					hasData = true;
					refreshList(result.data);
				}else{
					alert("无数据");
					hasData = false;
				}
			},
			dataType : "json"
		});
	}
	
	function removeModel(id){
		if(confirm("确定删除本条信息?")){
			var path = "<%=basePath%>removeMatchById";
			var data = {"id":id};
			$.ajax({
				type : 'POST',
				data : data,
				url : path,
				success : function(result) {
					getMatchs();
				},
				dataType : "json"
			});
		}
	}
	
	var smsPageNum = 1;
	var smsHasData = false;
	function getSMSs(){
		var path = "<%=basePath%>getSMSs";
		var data = {"pageNum":smsPageNum};
		$.ajax({
			type : 'POST',
			data : data,
			url : path,
			success : function(result) {
				$("#smsContent").empty();
				if(result.result == "ok"){
					smsHasData = true;
					$.each(result.data, function(n, value) {
						var content = value.content;
						var mobile = value.mobile;
						var statusCode = value.statusCode;
						var errorMsg = value.statusMsg;
						var time = "";
						if(value.timestamp != null){
							time = moment(value.timestamp.time).format("lll");
						}
						$("#smsContent").append("<tr><td>"+value.id+"</td><td>"+content+"</td><td>"+mobile+"</td><td>"+statusCode+"</td><td>"+errorMsg+"</td><td>"+time+"</td></tr>");
					});
				}else{
					alert("无数据");
					smsHasData = false;
				}
			},
			dataType : "json"
		});
	}
	
	function page(type){
		if(type == 0){
			//上一页
			if(pageNum == 1)return;
			pageNum--;
			
		}else{
			//下一页
			if(!hasData)
				return;
			pageNum++;
		}
		getMatchs();
	}
	
	function page2(type){
		if(type == 0){
			//上一页
			if(smsPageNum == 1)return;
			smsPageNum--;
		}else{
			//下一页
			if(!smsHasData)
				return;
			smsPageNum++;
		}
		getSMSs();
	}
	
	function refreshList(data){
		$("#content").empty();
		$.each(data, function(n, value) {
			var from = value.fromDistrict.city.name +" " +value.fromDistrict.name + " "+value.fromAddress;
			var to = value.toDistrict.city.name +" " +value.toDistrict.name+" "+value.toAddress;
			var time = value.time;
			var type = "";
			if(value.type == 0){
				type = "人找车";
			}else{
				type = "车找人";
			}
			var source = "";
			var name = "";
			var mobile = "";
			if(value.user != null){
				source = "微信用户";
				name = value.user.name;
				mobile = value.user.mobile;
			}else{
				source = "临时用户";
				name = value.temp_user.name;
				mobile = value.temp_user.mobile;
			}
			var price = value.price;
			var inputtime = "";
			if(value.update_time != null){
				inputtime = moment(value.update_time.time).format("lll");
			}
			$("#content").append("<tr><td>"+value.id+"</td><td>"+from+"</td><td>"+to+"</td><td>"+time+"</td><td>"+type+"</td><td>"+name+"</td><td>"+mobile+"</td><td>"+price+"</td>><td>"+source+"</td><td>"+inputtime+"</td><td><button onclick='removeModel("+value.id+");' class='am-btn am-btn-danger'>删除</button></td></tr>");
		});
	}
	
	function search(){
		var mobile = $("#mobile").val();
		if(mobile=="")return;
		var path = "<%=basePath%>search";
		var data = {"mobile":mobile};
		$.ajax({
			type : 'POST',
			data : data,
			url : path,
			success : function(result) {
				$("#mobile").val("");
				if(result.result == "ok"){
					refreshList(result.data);
				}else{
					alert("无数据");
				}
			},
			dataType : "json"
		});
	}
	
</script>

</head>
<body>

	<div style="margin-left: 10px;margin-top: 10px;">
		<ul id="top_search_ly" style="margin: 0px;padding: 0px;">
			<li><input style="width: 200px;" type="text" id="mobile" minlength="3" placeholder="输入要查询的手机号" class="am-form-field" /></li>
			<li style="margin-left: 10px;"><button type="button" onclick="search();" class="am-btn am-btn-secondary">提交</button></li>
		</ul>
	</div>
	
	<div style="clear: both;"></div>
	
	<div class="am-panel am-panel-primary" style="margin-top: 10px;">
		<div class="am-panel-hd">
			<h3 class="am-panel-title">匹配记录查看</h3>
		</div>
		<table
			class="am-table am-table-bordered am-table-striped am-text-nowrap">
			<tbody>
				<tr>
					<td>ID</td>
					<td>出发地</td>
					<td>目的地</td>
					<td>时间</td>
					<td>匹配类型</td>
					<td>联系人</td>
					<td>联系电话</td>
					<td>平摊油费</td>
					<td>来源</td>
					<td>录入时间</td>
					<td>操作</td>
				</tr>
			</tbody>
			<tbody id="content">

			</tbody>
		</table>
		<ul class="am-pagination">
			<li><a href="javascript:page(0);">&laquo;上一页</a></li>
			<li><a href="javascript:page(1);">下一页 &raquo;</a></li>
		</ul>
	</div>
	
	<div class="am-panel am-panel-primary">
		<div class="am-panel-hd">
			<h3 class="am-panel-title">短信发送记录</h3>
		</div>
		<table
			class="am-table am-table-bordered am-table-striped am-text-nowrap">
			<tbody>
				<tr>
					<td>ID</td>
					<td>内容</td>
					<td>手机</td>
					<td>状态码</td>
					<td>错误内容</td>
					<td>发送时间</td>
				</tr>
			</tbody>
			<tbody id="smsContent">

			</tbody>
		</table>
		<ul class="am-pagination">
			<li><a href="javascript:page2(0);">&laquo;上一页</a></li>
			<li><a href="javascript:page2(1);">下一页 &raquo;</a></li>
		</ul>
	</div>

</body>
</html>