<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet" />
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js" ></script>
<title>Insert title here</title>
<c:set var="contextPath" value="<%=request.getContextPath() %>"></c:set>
<style>
	.topHeader {
		background-image: url('${contextPath }/images/iceland.jpg');
		background-size: cover;
	}
	
	.userImg {
		 width: 30px;
		 height: 30px;
		 border-radius: 30px;
	}
</style>
</head>
<body>
<div class="p-5 bg-primary text-white text-center topHeader">
  <h1>JSP MiniProject</h1>
  <p>2024 Jan</p> 
</div>

<nav class="navbar navbar-expand-sm bg-dark navbar-dark">
  <div class="container-fluid">
    <ul class="navbar-nav">
      <li class="nav-item">
        <a class="nav-link active" href="${contextPath }/index.jsp">Websky</a>
      </li>
      <li class="nav-item">
        <a class="nav-link" href="${contextPath }/board/listAll.bo">게시판</a>
      </li>
      
      <c:choose>
      	<c:when test="${sessionScope.loginUser == null }">
		      <li class="nav-item">
		        <a class="nav-link" href="${contextPath }/member/register.jsp">회원가입</a>
		      </li>
		      <li class="nav-item">
		        <a class="nav-link " href="${contextPath }/member/login.jsp">로그인</a>
		      </li>
	      </c:when>
	      <c:otherwise>
		      <li class="nav-item" style="color: white;">
		        <a class="nav-link" href="${contextPath }/member/myPage.mem?userId=${sessionScope.loginUser.userId}" > 
		        	${sessionScope.loginUser.userId }
		        	<img src="${contextPath }/${sessionScope.loginUser.memberImg}" class ="userImg"/>
		        </a>
		      </li>
		      <li class="nav-item">
		        <a class="nav-link " href="${contextPath }/member/logout.mem">로그아웃</a>
		      </li>
	      </c:otherwise>
      </c:choose>
      <c:if test="${sessionScope.loginUser.isAdmin == 'Y' }">
     		<li class="nav-item">
		        <a class="nav-link " href="${contextPath }/admin/admin.jsp">관리자페이지</a>
		    </li>
      </c:if>
      
    </ul>
  </div>
</nav>
</body>
</html>