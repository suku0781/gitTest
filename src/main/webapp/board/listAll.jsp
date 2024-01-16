<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
<title>게시판</title>
<!-- 작성시간이 5시간 전이면 new.png를 제목 옆에 붙여준다. (js) -->

<script type="text/javascript">
	$(function(){
		$(".board").each(function(){
			console.log($(this).children().eq(3).html());
			let curDate = new Date();
			let postDate = new Date($(this).children().eq(3).html());
			let diff = (curDate - postDate) / 1000 / 60 / 60; // 시간 단위
			console.log(diff);
			let title = $(this).children().eq(1).html();
			if (diff < 4){
				let output = "<span><img src='../images/new.png'/></span>";
				$(this).children().eq(1).html(title + output);
			}
		});
		
		
		
	});
	



</script>
<style>
	span img {
		width: 35px;
		padding-left: 10px;
	}
	
	.delBoard td{
		color: gray;
 		text-decoration : line-through; 
	} 
	
	.searchArea {
		display:flex;
		justify-content: flex-end;
	}
	
</style>


</head>
<body>
<c:set var="contextPath" value="<%=request.getContextPath() %>"></c:set>
<jsp:include page="../header.jsp"></jsp:include>
	<div class="container">
	<h1>게시판 전체목록</h1>
	<div class="boardList">
		<c:choose>
			<c:when test="${boardList != null }">
				<table class="table table-hover">
				    <thead>
				      <tr>
				        <th>글번호</th>
				        <th>제 목</th>
				        <th>작성자</th>
				        <th>작성일</th>
				        <th>조회수</th>
				        <th>ref</th>
				        <th>step</th>
				        <th>reforder</th>
				      </tr>
				    </thead>
			    	<tbody>
			    		<c:forEach var="board" items="${boardList }">
			    			<c:choose>
			    				<c:when test="${board.isDelete == 'N' }">
			    					<tr id='board${board.no }' class='board' onclick="location.href='viewBoard.bo?no=${board.no}'">
					    				<td>${board.no }</td>
					    				<td>
					    					<c:if test="${board.step > 0 }">
							    				   <c:forEach var="i" begin="1" end="${board.step }" varStatus="status">
							    				   		<c:if test="${status.last }">
							    				   			<img src="${contextPath }/images/reply.png" width="20px" style="margin-left: calc(20px * ${i}) ">
							    				   		</c:if>
							    				   </c:forEach>
					    					</c:if>
							    				   	${board.title }</td>
					    					
					    				<td>${board.writer }</td>
					    				<td>${board.postDate }</td>
					    				<td>${board.readcount }</td>
					    				<td>${board.ref }</td>
					    				<td>${board.step }</td>
					    				<td>${board.reforder }</td>
				    				</tr>
			    				</c:when>
			    				<c:otherwise>
					    			<tr id='board${board.no }' class='board delBoard'>
					    				<td>${board.no }</td>
					    				<td class="" style="color: gray;">
					    					<c:if test="${board.step > 0 }">
							    				   <c:forEach var="i" begin="1" end="${board.step }">
							    				   		<img src="${contextPath }/images/reply.png" width="30px">
							    				   </c:forEach>
					    					</c:if>
							    				   	${board.title }(삭제된 글입니다)</td>
					    					
					    				<td>${board.writer }</td>
					    				<td>${board.postDate }</td>
					    				<td>${board.readcount }</td>
					    				<td>${board.ref }</td>
					    				<td>${board.step }</td>
					    				<td>${board.reforder }</td>
					    			</tr>
				    			</c:otherwise>
			    			</c:choose>
			    		</c:forEach>
    				</tbody>
			  </table>
			</c:when>
			<c:otherwise>
				<div style="font-size : 150px;">텅~~!</div>
			</c:otherwise>
		</c:choose>
	</div>
	<div class="btns">
		<button type="button" class="btn btn-success" onclick="location.href='writeBoard.jsp'">글쓰기</button>
	</div>
	
	
	
	
	<div>${pagingInfo }</div>
	<div>${param}</div>
<%-- 	<div>${param.pageNo == null }</div> --%>
	<div class="mt-3 paging">
		<ul class="pagination">
			<c:if test="${pagingInfo.pageNo == 1 }">
				<li class="page-item disabled"><a class="page-link" href="#">Previous</a></li>
			</c:if>
			
			<c:if test="${param.pageNo > 1 }">
		    	<li class="page-item"><a class="page-link" href="listAll.bo?pageNo=${param.pageNo - 1}&searchType=${param.searchType}&searchWord=${param.searchWord}">Previous</a></li>
		    </c:if>
		
		    <c:forEach var="i" begin="${requestScope.pagingInfo.startNumOfCurrentPagingBlock}" 
		    			end="${requestScope.pagingInfo.endNumOfCurrentPagingBlock}">
		    			
		    	<c:choose>
		    		<c:when test="${pagingInfo.pageNo == i}">
		    			<li class="page-item"><a class="page-link active" href="listAll.bo?pageNo=${i}&searchType=${param.searchType}&searchWord=${param.searchWord}">${i}</a></li>
		    		</c:when>
		    		<c:otherwise>
		    			<li class="page-item"><a class="page-link" href="listAll.bo?pageNo=${i}&searchType=${param.searchType}&searchWord=${param.searchWord}">${i}</a></li>
		    		</c:otherwise>
		    	</c:choose>		
		    </c:forEach>
		    
		   <c:choose> 
			    <c:when test="${pagingInfo.pageNo < requestScope.pagingInfo.totalPageCnt }">
			    	<li class="page-item"><a class="page-link" href="listAll.bo?pageNo=${pagingInfo.pageNo + 1}&searchType=${param.searchType}&searchWord=${param.searchWord}">Next</a></li>
			    </c:when>
			    <c:when test="${pagingInfo.pageNo == requestScope.pagingInfo.totalPageCnt}">
			    	<li class="page-item disabled"><a class="page-link" href="#">Next</a></li>
			    </c:when>
		    </c:choose>
	  </ul>
	 </div>
	
	<!--  검색타입(작성자, 제목, 본문)과 검색어 입력 -->
	<div class="mb-3 mt-3 searchItem">
		<form action="listAll.bo" method="get" class="searchArea">
			<div style="width: 150px;">
				<select class="form-select" name="searchType">
				  <option value="writer">작성자</option>
				  <option value="title">제목</option>
				  <option value="content">본문</option>
				</select>
			</div>
			<div>
				<div class="input-group mb-3" style="width: 350px;">
				  <input type="text" class="form-control" placeholder="Search" name="searchWord">
				  <button class="btn btn-success" type="submit">검색</button>
				</div>
			</div>
		</form>
	</div>
	
	
	
	
	
	
	
	
	
	
	
	
</div>
<jsp:include page="../footer.jsp"></jsp:include>
</body>
</html>