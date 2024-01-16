<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시판 답글</title>
</head>
<body>
	<jsp:include page="../header.jsp"></jsp:include>
	<c:if test="${sessionScope.loginUser == null }">
		<c:redirect url="../member/login.jsp"></c:redirect>
	</c:if>
	
	<div class="container">
		<h1>게시판 답글 작성</h1>
		<form action="reply.bo" method="post" >
			<div class="mb-3 mt-3">
			    <label for="writer" class="form-label">작성자:</label>
			    <input type="text" class="form-control" id="writer" name="writer" value="${sessionScope.loginUser.userId}" readonly>
			</div>
			
			<div class="mb-3 mt-3">
			    <label for="title" class="form-label">제목:</label>
			    <input type="text" class="form-control" id="title" name="title">
			</div>
			
			<div class="mb-3 mt-3">
			    <label for="content" class="form-label">내용:</label>
			    <textarea rows="10" style="width: 100%" id="content" name="content"></textarea>
			</div>
			
			<input type="hidden" name="ref" value="${param.ref }" />
			<input type="hidden" name="step" value="${param.step }" />
			<input type="hidden" name="reforder" value="${param.reforder }" />
			
			<button type="submit" class="btn btn-success">저장</button>
			<button type="reset" class="btn btn-danger" onclick="location.href='listAll.bo'">취소</button>
		
		</form>
	</div>
	
	
<jsp:include page="../footer.jsp"></jsp:include>
	
</body>
</html>