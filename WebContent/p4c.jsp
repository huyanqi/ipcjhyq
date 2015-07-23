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
	content="width=device-width, initial-scale=1, maximum-scale=1, minimum-scale=1, user-scalable=no">
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
</style>

<title>人找车-信息登记</title>

<script type="text/javascript">

	$(document).ready(function() {
		
		//省份选择监听
		$('#select-native-1').change(function(){ 
			getCityByProvince($("#select-native-1").val(),0);
		});
		$('#select-native-4').change(function(){ 
			getCityByProvince($("#select-native-4").val(),1);
		});
		//城市选择监听
		$('#select-native-2').change(function(){
			getDistrictByCity($("#select-native-2").val(),0);
		});
		
		$('#select-native-5').change(function(){ 
			getDistrictByCity($("#select-native-5").val(),1);
		});
		
		$('#datetimepicker').datetimepicker({lang:'ch'});
		
		getAllProvince();//获取所有省份信息
		getMyUserInfo();//获取我已有的信息
	});
	
	function getUrlParam(name) {
	    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
	    var r = window.location.search.substr(1).match(reg);  //匹配目标参数
	    if (r != null) return unescape(r[2]); return null; //返回参数值
	}
	
	function getMyUserInfo(){
		var path = "<%=basePath%>getMyUserInfo";
		var data = {};
		$.ajax({
			type : 'POST',
			data : data,
			url : path,
			success : function(result) {
				if(result.result == "ok"){
					$("#name").val(result.data.name);
					$("#mobile").val(result.data.mobile);
				}
			},
			dataType : "json"
		});
	}
	
	function noNumbers(e){
		var keynum
		var keychar
		var numcheck
		if (window.event) // IE
		{
			keynum = e.keyCode
		}
		else if (e.which) // Netscape/Firefox/Opera
		{
			keynum = e.which
		}
		keychar = String.fromCharCode(keynum);
		//判断是数字,且小数点后面只保留两位小数
		if (!isNaN(keychar)) {
			var index = e.currentTarget.value.indexOf(".");
			if (index >= 0 && e.currentTarget.value.length - index > 2) {
				return false;
			}
			return true;
		}
		//如果是小数点 但不能出现多个 且第一位不能是小数点
		if ("." == keychar) {
			if (e.currentTarget.value == "") {
				return false;
			}
			if (e.currentTarget.value.indexOf(".") >= 0) {
				return false;
			}
			return true;
		}
		return false;
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
				$("#select-native-1").trigger("change");
				
				$.each(result.data, function(n, value) {
					$("#select-native-4").append("<option value="+value.id+">"+value.name+"</option>");
				});
				$("#select-native-4").trigger("change");
			},
			dataType : "json"
		});
	}
	
	//type:0 出发地 1:目的地
	function getCityByProvince(provinceId,type){
		$.AMUI.progress.start();
		var path = "<%=basePath%>getCityByProvince";
		var data = {"provinceId":provinceId};
		$.ajax({
			type : 'POST',
			data : data,
			url : path,
			success : function(result) {
				$.AMUI.progress.done();
				if(type == 0){
					//更新出发地
					$("#select-native-2").empty();
					$.each(result.data, function(n, value) {
						$("#select-native-2").append("<option value="+value.id+">"+value.name+"</option>");
					});
				}else if(type == 1){
					//更新目的地
					$("#select-native-5").empty();
					$.each(result.data, function(n, value) {
						$("#select-native-5").append("<option value="+value.id+">"+value.name+"</option>");
					});
				}
			},
			dataType : "json"
		});
	}
	
	//type 0:出发地 1:目的地
	function getDistrictByCity(cityId,type){
		$.AMUI.progress.start();
		var path = "<%=basePath%>getDistrictByCity";
		var data = {"cityId":cityId};
		$.ajax({
			type : 'POST',
			data : data,
			url : path,
			success : function(result) {
				$.AMUI.progress.done();
				if(type == 0){
					$("#select-native-3").empty();
					$.each(result.data, function(n, value) {
						$("#select-native-3").append("<option value="+value.id+">"+value.name+"</option>");
					});
				}else if(type == 1){
					$("#select-native-6").empty();
					$.each(result.data, function(n, value) {
						$("#select-native-6").append("<option value="+value.id+">"+value.name+"</option>");
					});
				}
			},
			dataType : "json"
		});
	}
	
	function match(){
		
		//获取出发省份ID
		var fromProvinceId = $("#select-native-1").val();
		//出发省份name
		var fromProvinceName = $("#select-native-1").find("option:selected")
				.text();
		//出发城市ID
		var fromCityId = $("#select-native-2").val();
		//出发城市name
		var fromCityName = $("#select-native-2").find("option:selected").text();
		//出发地区ID
		var fromDistrictId = $("#select-native-3").val();
		//出发地区name
		var fromDistrictName = $("#select-native-3").find("option:selected")
				.text();
		//出发详细地址
		var fromAddress = $("#fromAddress").val();
		

		//获取到达省份ID
		var toProvinceId = $("#select-native-4").val();
		//到达省份name
		var toProvinceName = $("#select-native-4").find("option:selected")
				.text();
		//到达城市ID
		var toCityId = $("#select-native-5").val();
		//到达城市name
		var toCityName = $("#select-native-5").find("option:selected").text();
		//到达地区ID
		var toDistrictId = $("#select-native-6").val();
		//到达地区name
		var toDistrictName = $("#select-native-6").find("option:selected")
				.text();
		//到达详细地址
		var toAddress = $("#toAddress").val();

		//联系人名字
		var name = $("#name").val();
		//联系人电话
		var mobile = $("#mobile").val();

		//组装USER对象
		var user = '{"name":"' + name + '","mobile":"' + mobile + '"}';
		//user = eval('(' + user + ')');

		//组装出发区域
		//出发省份
		var fromProvince = '{"id":' + fromProvinceId + ',"name":"'
				+ fromProvinceName + '"}';

		//出发城市
		var fromCity = '{"id":' + fromCityId + ',"name":"' + fromCityName
				+ '","province":' + fromProvince + '}';
		//fromCity = eval('(' + fromCity + ')');

		//出发地区
		var fromDistrict = '{"id":' + fromDistrictId + ',"name":"'
				+ fromDistrictName + '","city":' + fromCity + '}';

		//fromDistrict = eval('(' + fromDistrict + ')');

		//到达省份
		var toProvince = '{"id":' + toProvinceId + ',"name":"' + toProvinceName
				+ '"}';
		//到达城市
		var toCity = '{"id":' + toCityId + ',"name":"' + toCityName
				+ '","province":' + toProvince + '}';

		//到达地区
		var toDistrict = '{"id":' + toDistrictId + ',"name":"' + toDistrictName
				+ '","city":' + toCity + '}';
		//toDistrict = eval('(' + toDistrict + ')');

		var time = $("#datetimepicker").val();

		var path = "<%=basePath%>p4c";
		var data = '{"toAddress":"'+toAddress+'","fromAddress":"'+fromAddress+'","time":"' + time + '","fromDistrict":' + fromDistrict
				+ ',"toDistrict":' + toDistrict + ',"user":' + user
				+ ',"type":0}';

		var $btn = $("#submit");
		$btn.button('loading');
		$.ajax({
			type : 'POST',
			dataType : "json",
			contentType : "application/json ; charset=utf-8",
			data : data,
			url : path,
			success : function(result) {
				$btn.button('reset');
				if (result.result == "ok") {
					alert("匹配信息成功录入，匹配成功后我们将发送通知到你的微信");
					wx.closeWindow();
				} else {
					alert(result.data);
				}
			},
			dataType : "json"
		});
	}
</script>

</head>
<body>

	<header class="am-topbar">
	<h1 class="am-topbar-brand">
		<a href="#">人找车</a>
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
			<td id="address" align="right">出发地：</td>
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

			<td>
				<div class="am-form-group am-form-select">
					<select name="select-native-3" id="select-native-3"
						data-am-selected="{btnWidth: '100%', btnSize: 'sm', btnStyle: 'secondary',maxHeight: 200}">

					</select>
				</div>
			</td>
		</tr>
		
		<tr>
			<td id="address">出发地址：</td>
			<td colspan="3"><input id="fromAddress" type="text" class="am-form-field" placeholder="(选填)"/></td>
		</tr>

		<tr>
			<td align="right">目的地：</td>
			<td>
				<div class="am-form-group am-form-select">
					<select name="select-native-1" id="select-native-4"
						data-am-selected="{btnWidth: '100%', btnSize: 'sm', btnStyle: 'secondary',maxHeight: 200}">
						        
					</select>
				</div>
			</td>

			<td>
				<div class="am-form-group am-form-select">
					<select name="select-native-2" id="select-native-5"
						data-am-selected="{btnWidth: '100%', btnSize: 'sm', btnStyle: 'secondary',maxHeight: 200}">
						        
					</select>
				</div>
			</td>

			<td>
				<div class="am-form-group am-form-select">
					<select name="select-native-3" id="select-native-6"
						data-am-selected="{btnWidth: '100%', btnSize: 'sm', btnStyle: 'secondary',maxHeight: 200}">
						        
					</select>
				</div>
			</td>
		</tr>
		<tr>
			<td id="address">目的地址：</td>
			<td colspan="3"><input id="toAddress" type="text" class="am-form-field" placeholder="(选填)"/></td>
		</tr>
		<tr>
			<td align="right">出发时间：</td>
			<td colspan="3"><input id="datetimepicker" type="text"
				class="am-form-field" readonly="readonly" /></td>
		</tr>
		<tr>
			<td align="right">联系人名:</td>
			<td colspan="3"><input type="text" id="name"
				class="am-form-field" /></td>
		</tr>
		<tr>
			<td align="right">联系电话:</td>
			<td colspan="3"><input type="number" id="mobile"
				class="am-form-field" onkeypress="return noNumbers(event)"/></td>
		</tr>
	</table>

	<button type="button" id="submit"
		class="am-btn am-btn-secondary btn-loading-example" onclick="match();"
		style="width: 100%;"
		data-am-loading="{spinner: 'circle-o-notch', loadingText: '努力提交中', resetText: '马上匹配'}">马上匹配</button>

</body>
</html>