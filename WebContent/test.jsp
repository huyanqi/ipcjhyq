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
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="description" content="">
  <meta name="keywords" content="">
  <meta name="viewport"
        content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
  <title>Hello Amaze UI</title>

  <meta name="renderer" content="webkit">
  <meta http-equiv="Cache-Control" content="no-siteapp"/>
  <meta name="mobile-web-app-capable" content="yes">
  <meta name="apple-mobile-web-app-capable" content="yes">
  <meta name="apple-mobile-web-app-status-bar-style" content="black">
  <meta name="apple-mobile-web-app-title" content="Amaze UI"/>
  <meta name="msapplication-TileImage" content="<%=basePath%>js/amazeui/assets/i/app-icon72x72@2x.png">
  <meta name="msapplication-TileColor" content="#0e90d2">
  
  <link rel="icon" type="image/png" href="<%=basePath%>js/amazeui/assets/i/favicon.png">
  <link rel="icon" sizes="192x192" href="<%=basePath%>js/amazeui/assets/i/app-icon72x72@2x.png">
  <link rel="apple-touch-icon-precomposed" href="<%=basePath%>js/amazeui/assets/i/app-icon72x72@2x.png">
  <link rel="stylesheet" href="<%=basePath%>js/amazeui/assets/css/amazeui.min.css">
  <link rel="stylesheet" href="<%=basePath%>js/amazeui/assets/css/app.css">
  
  <script type="text/javascript">
  	$(document).ready(function(){
  		
  	});
  </script>
  
</head>
<body>

<table class="am-table am-table-bordered am-table-radius am-table-striped">
    <thead>
        <tr>
            <th>网站名称</th>
            <th>网址</th>
            <th>创建时间</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td>Amaze UI</td>
            <td>http://amazeui.org</td>
            <td>2012-10-01</td>
        </tr>
        <tr>
            <td>Amaze UI</td>
            <td>http://amazeui.org</td>
            <td>2012-10-01</td>
        </tr>
        <tr class="am-active">
            <td>Amaze UI(Active)</td>
            <td>http://amazeui.org</td>
            <td>2012-10-01</td>
        </tr>
        <tr>
            <td>Amaze UI</td>
            <td>http://amazeui.org</td>
            <td>2012-10-01</td>
        </tr>
        <tr>
            <td>Amaze UI</td>
            <td>http://amazeui.org</td>
            <td>2012-10-01</td>
        </tr>
    </tbody>
</table>

<script src="<%=basePath%>js/amazeui/assets/js/jquery.min.js"></script>
<script src="<%=basePath%>js/amazeui/assets/js/amazeui.min.js"></script>
</body>
</html>