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

<title>地址管理</title>

<script type="text/javascript">

var province;
var city;

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
					getAllProvince();//获取所有省份信息
					
					//省份选择监听
					$('#select-native-1').change(function(){ 
						getCityByProvince($("#select-native-1").val(),0);
					});
					
					//城市选择监听
					$('#select-native-2').change(function(){
						getDistrictByCity($("#select-native-2").val(),0);
						$("#insertBtn").html("新增 "+$("#select-native-2 option:selected").text()+" 常用地区");
					});
				}
			},
			dataType : "json"
		});
	}
	
	function getAllProvince() {
		$.AMUI.progress.start();
		var path = "<%=basePath%>getProvinces";
		var data = {};
		$.ajax({
			type : 'POST',
			data : data,
			url : path,
			success : function(result) {
				$.AMUI.progress.done();
				$.each(result.data, function(n, value) {
					$("#select-native-1").append("<option value="+value.id+">"+value.name+"</option>");
				});
			},
			dataType : "json"
		});
	}
	
	function getCityByProvince(provinceId){
		province = provinceId;
		$.AMUI.progress.start();
		var path = "<%=basePath%>getCityByProvince";
		var data = {"provinceId":provinceId};
		$.ajax({
			type : 'POST',
			data : data,
			url : path,
			success : function(result) {
				$.AMUI.progress.done();
					//更新出发地
					$("#select-native-2").empty();
					$.each(result.data, function(n, value) {
						$("#select-native-2").append("<option value="+value.id+">"+value.name+"</option>");
					});
			},
			dataType : "json"
		});
	}
	
	function getDistrictByCity(cityId){
		city = cityId;
		$.AMUI.progress.start();
		var path = "<%=basePath%>getDistrictByCity";
		var data = {
			"cityId" : cityId
		};
		$.ajax({
			type : 'POST',
			data : data,
			url : path,
			success : function(result) {
				$.AMUI.progress.done();
				$("#list").empty();
				$.each(result.data, function(n, value) {
					$('#list').append("<li class='am-g am-list-item-dated'><a href='##' class='am-list-item-hd '>"+value.name+"</a> <span class='am-list-date'><a href='javascript:updateWeight("+value.id+","+value.weight+")' style='float:left;' >"+value.weight+"</a><a href='#' style='float:left;' onclick='javascript:removeRedirect("+value.id+")'>删除</a></span></li>");
				});
			},
			dataType : "json"
		});
	}
	
	function updateWeight(id,currentweight){
		$("#weight_number").val(currentweight);
		var weight = prompt(" 输入新的权重值(数字):",currentweight);

		if (weight!=null){
			$.AMUI.progress.start();
			var path = "<%=basePath%>updateDistrictWeight";
			var data = {"id" : id,"weight":weight};
			$.ajax({
				type : 'POST',
				data : data,
				url : path,
				success : function(result) {
					$.AMUI.progress.done();
					if(result.result == "ok"){
						getDistrictByCity(city);
					}
				},
				dataType : "json"
			});
	  	}
	}

	function removeRedirect(id, name) {
		if (confirm("确定删除?")) {
			$.AMUI.progress.start();
			var path = "<%=basePath%>removeDistrictById";
			var data = {"id" : id};
			$.ajax({
				type : 'POST',
				data : data,
				url : path,
				success : function(result) {
					$.AMUI.progress.done();
					if(result.result == "ok"){
						getDistrictByCity(city);
					}
				},
				dataType : "json"
			});
		}
	}
	
	function newredirect(){
		var provinceModel = '{"id":'+province+'}';
		var cityModel = '{"id":'+city+',"province":'+provinceModel+'}';
		if($("#newredirect").val() == ""){
			return;
		}
		$.AMUI.progress.start();
		$.ajax({
			type : 'POST',
			dataType : "json",
			contentType : "application/json ; charset=utf-8",
			data : '{"name":"'+$("#newredirect").val()+'","city":'+cityModel+'}',
			url : "<%=basePath%>insertDistrict",
			success : function(result) {
				$.AMUI.progress.done();
				if (result.result == "ok") {
					$("#newredirect").val("");
					getDistrictByCity(city);
				} else {
					alert("添加失败");
				}
			},
			dataType : "json"
		});
	}
</script>

</head>
<body>

	<div style="width: 100%; height: 100%; margin: auto;">
		<header class="am-topbar">
		<h1 class="am-topbar-brand">
			<a href="#">地区管理</a>
		</h1>

		<div class="am-collapse am-topbar-collapse" id="doc-topbar-collapse">
			<ul class="am-nav am-nav-pills am-topbar-nav">
				<li><a href="#"></a></li>
			</ul>

		</div>
		</header>

		<table style="width: 100%; margin-top: 20px;"
			class="am-table am-table-bordered am-table-striped am-text-nowrap">
			<tr>
				<td align="right">选择城市：</td>
				<td>
					<div class="am-form-group am-form-select">
						<select name="select-native-1" id="select-native-1"
							data-am-selected="{btnWidth: '100%', btnSize: 'sm', btnStyle: 'secondary',maxHeight: 200}">
						</select>
					</div>
				</td>
				<td>
					<div class="am-form-group am-form-select">
						<select name="select-native-2" id="select-native-2"
							data-am-selected="{btnWidth: '100%', btnSize: 'sm', btnStyle: 'secondary',maxHeight: 200}">
						</select>
					</div>
				</td>
			</tr>
			<tr>
				<td></td>
				<td>
					<input type="text" class="am-form-field" id="newredirect">
				</td>
				<td>
					<button class="am-btn am-btn-secondary" onclick="newredirect();" id="insertBtn">新增常用地区</button>
				</td>
			</tr>
			<tr>
				<td align="right">管理地区</td>
				<td colspan="2">
					<div data-am-widget="list_news"
						class="am-list-news am-list-news-default">
						<div class="am-list-news-bd">
							<ul class="am-list" id="list">
							</ul>
						</div>
					</div>
				</td>
			</tr>
		</table>

	</div>
	
	<div class="am-modal am-modal-prompt" tabindex="-1" id="my-prompt">
  <div class="am-modal-dialog">
    <div class="am-modal-hd">i拼车</div>
    <div class="am-modal-bd">
     输入新的权重值(数字):
      <input type="text" class="am-modal-prompt-input" id="weight_number">
    </div>
    <div class="am-modal-footer">
      <span class="am-modal-btn" data-am-modal-cancel>提交</span>
      <span class="am-modal-btn" data-am-modal-confirm>取消</span>
    </div>
  </div>
</div>
</body>
</html>