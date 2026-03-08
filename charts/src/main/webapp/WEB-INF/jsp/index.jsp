<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>인증서 통계 대시보드</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        h1 { color: #333; }
        ul { list-style-type: none; padding: 0; }
        li { margin: 10px 0; }
        a { text-decoration: none; color: #007bff; font-size: 18px; }
        a:hover { text-decoration: underline; }
    </style>
</head>
<body>
    <h1>인증서 통계 대시보드</h1>
    <p>다음 통계 화면을 선택하세요:</p>
    <ul>
        <li><a href="/chart">계약종류별 사용이력 통계</a></li>
        <li><a href="/chartSum">계약종류별 합계 사용이력 통계</a></li>
    </ul>
</body>
</html>
