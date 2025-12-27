<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Welcome to Spring Boot JSP</title>
</head>
<body>
    <h1>Hello, World!</h1>
    <p>This is a JSP page served by Spring Boot.</p>
    <p>Current time: <%= new java.util.Date() %></p>
</body>
</html>
