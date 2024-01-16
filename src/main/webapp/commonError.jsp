<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>공통 예외</title>
</head>
<body>
	<jsp:include page="header.jsp"></jsp:include>
	<div class="container"></div>
		<h1>commonError.jsp</h1>
		<div>${requestScope.errorMsg }</div>
		<hr />
		
		<c:forEach var="err" items="${requestScope.errorStack }">
			<div>${err }</div>
		</c:forEach>
		
	
	<jsp:include page="footer.jsp"></jsp:include>
</body>
</html>