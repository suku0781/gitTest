<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>마이페이지</title>
<style>
	.userPhoto {
	display: flex;
	justify-content: center;
	}
	
	.pointInfo{
	display: flex;
	justify-content: space-between;}
</style>
</head>
<body>
<c:set var="contextPath" value="<%=request.getContextPath() %>"></c:set>
<jsp:include page="../header.jsp"></jsp:include>
	<div class="container">
		<h1>마이페이지</h1>
		${requestScope.memberInfo }
		<div class="mb-3 mt-3 userPhoto" >
			<img alt="멤버이미지" src="${contextPath }/${requestScope.memberInfo.memberImg }">
		</div>
		<div class="mb-3 mt-3 userPhoto">
			<a href=''>이미지변경</a>
		</div>
	
		<div class="mb-3 mt-3">
			    <label for="userId" class="form-label">아이디:</label>
			    <input type="text" class="form-control" id="userId" value="${requestScope.memberInfo.userId }" readonly>
			</div>
			
			<div class="mb-3 mt-3">
			    <label for="userEmail" class="form-label">이메일:</label>
			    <input type="text" class="form-control" id="userEmail" value="${requestScope.memberInfo.userEmail }">
			    <button type="button" class="btn btn-warning sendMail" >이메일수정</button>
			</div>
		
		<div class="input-group mb-3">
		  <input type="text" class="form-control" value="${requestScope.memberInfo.userEmail }">
		  <button class="btn btn-success" type="submit">수정</button>
		</div>
		
		<div class="mb-3 mt-3 pointLog" >
			<div class="pointInfo">
				<h2>적립금 내역</h2>
				<p>총 적립금 : <span>${requestScope.memberInfo.userPoint }</span></p>
			</div>
			
			  <table class="table table-striped">
			  <thead>
			      <tr>
			        <th>적립일시</th>
			        <th>적립사유</th>
			        <th>적립포인트</th>
			      </tr>
			   </thead>
			  	<tbody>
<!-- 			  	pointLog -->
					<c:forEach var="point" items="${requestScope.pointLog }">
	      				<tr>
	      					<td>${point.when }</td>
	      					<td>${point.why }</td>
	      					<td>${point.howmuch }</td>
	      				</tr>
					</c:forEach>
    			</tbody>
			  </table>		
		</div>
		
	<button class="btn btn-danger" name="deleteAccount" value="${requestScope.login.userId }">Delete Account</button>
	</div>
<jsp:include page="../footer.jsp"></jsp:include>	
</body>
</html>