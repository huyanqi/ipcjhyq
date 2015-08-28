<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>i拼车 - 内容管理系统</title>
    <meta content='width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no' name='viewport' />
    
    <!--[if lt IE 9]>
    <script src='<%=basePath%>js/html5shiv.js' type='text/javascript'></script>
    <![endif]-->
    <link href='<%=basePath%>js/stylesheets/bootstrap/bootstrap.css' media='all' rel='stylesheet' type='text/css' />
    <link href='<%=basePath%>js/stylesheets/bootstrap/bootstrap-responsive.css' media='all' rel='stylesheet' type='text/css' />
    <!-- / jquery ui -->
    <link href='<%=basePath%>js/stylesheets/jquery_ui/jquery-ui-1.10.0.custom.css' media='all' rel='stylesheet' type='text/css' />
    <link href='<%=basePath%>js/stylesheets/jquery_ui/jquery.ui.1.10.0.ie.css' media='all' rel='stylesheet' type='text/css' />
    <!-- / flatty theme -->
    <link href='<%=basePath%>js/stylesheets/light-theme.css' id='color-settings-body-color' media='all' rel='stylesheet' type='text/css' />
    <!-- / demo -->
    <link href='<%=basePath%>js/stylesheets/demo.css' media='all' rel='stylesheet' type='text/css' />
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" /></head>
<body class='contrast-red '>
<header>
    <div class='navbar' id='navbar'>
        <div class='navbar-inner'>
            <div class='container-fluid'>
                <a class='brand' href='#'>
                    <i class='icon-heart-empty'></i>
                    <span class='hidden-phone'>i拼车</span>
                </a>
                <a class='toggle-nav btn pull-left' href='#'>
                    <i class='icon-reorder'></i>
                </a>
            </div>
        </div>
    </div>
</header>
<div id='wrapper'>
<div id='main-nav-bg'></div>
<nav class='' id='main-nav'>
<div class='navigation'>
<div class='search'>
    <form accept-charset="UTF-8" action="search_results.html" method="get" /><div style="margin:0;padding:0;display:inline"><input name="utf8" type="hidden" value="&#x2713;" /></div>
        <div class='search-wrapper'>
            <input autocomplete="off" class="search-query" id="q" name="q" placeholder="Search..." type="text" value="" />
            <button class="btn btn-link icon-search" name="button" type="submit"></button>
        </div>
    </form>
</div>
<ul class='nav nav-stacked'>
<li class=''>
    <a href='javascript:frameLink("fastInsert.jsp")'>
        <i class='icon-tint'></i>
        <span>快速录入</span>
    </a>
</li>
<li class=''>
    <a href='javascript:frameLink("NewMatchs.jsp")'>
        <i class='icon-tint'></i>
        <span>最新匹配记录</span>
    </a>
</li>
<li class=''>
    <a href='javascript:frameLink("AddressManage.jsp")'>
        <i class='icon-tint'></i>
        <span>地址管理</span>
    </a>
</li>
</ul>
</div>
</nav>
<div id="content">
<iframe src="fastInsert.jsp" id="myframe" width="100%" height="100%">
            
</iframe>
</div>
</div>
<!-- / jquery -->
<script src='<%=basePath%>js/jquery/jquery.min.js' type='text/javascript'></script>
<!-- / jquery mobile events (for touch and slide) -->
<script src='<%=basePath%>js/mobile_events/jquery.mobile-events.min.js' type='text/javascript'></script>
<!-- / jquery migrate (for compatibility with new jquery) -->
<script src='<%=basePath%>js/jquery/jquery-migrate.min.js' type='text/javascript'></script>
<!-- / jquery ui -->
<script src='<%=basePath%>js/jquery_ui/jquery-ui.min.js' type='text/javascript'></script>
<!-- / bootstrap -->
<script src='<%=basePath%>js/bootstrap/bootstrap.min.js' type='text/javascript'></script>
<script src='<%=basePath%>js/jquery/nav.js' type='text/javascript'></script>
<script type="text/javascript">

	$(document).ready(function(){
		$("#content").height($(document).height() - $("#navbar").height());
	});

	function frameLink(page){
		$("#myframe").attr("src",page);
	}
</script>
</body>
</html>
