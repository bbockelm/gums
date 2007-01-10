<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="gov.bnl.gums.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
  <title>GUMS</title>
  <link href="gums.css" type="text/css" rel="stylesheet">
</head>
<body>
<%@include file="topNav.jspf"%>
<div id="title">
<h1><span>GUMS</span></h1>
<h2><span>GUMS version 1.2</span></h2>
</div>
<%@include file="sideNav.jspf"%>
<div id="body">
<h1>Welcome to GUMS: Grid User Management System.</h1>
<p>GUMS allows for central management of grid identity to local account mappings.
This web application contains both the web service components and the web interface.
On your left you see a series of commands you can execute to configure the mappings, 
manage users, and test mappings.</p>
</div>
<%@include file="bottomNav.jspf"%>
</body>
</html>
