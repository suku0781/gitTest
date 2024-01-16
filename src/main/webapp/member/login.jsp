<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>로그인</title>
</head>
<body>
	<jsp:include page="../header.jsp"></jsp:include>
	<div class="container">
		<h1>로 그 인</h1>
		
		<form action="login.mem" method="post" >
			<div class="mb-3 mt-3">
			    <label for="userId" class="form-label">아이디:</label>
			    <input type="text" class="form-control" id="userId" placeholder="Enter your id" name="userId">
			</div>
			
			<div class="mb-3 mt-3">
			    <label for="userPwd" class="form-label">비밀번호:</label>
			    <input type="password" class="form-control" id="userPwd" placeholder="Enter your password" name="userPwd">
			</div>
			
			<button type="submit" class="btn btn-success">로그인</button>
			<button type="reset" class="btn btn-danger">취소</button>
		
		</form>
		
		
		
	</div>
<jsp:include page="../footer.jsp"></jsp:include>
</body>
</html>