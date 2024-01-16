<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
<title>게시글</title>
<script type="text/javascript">
	$(function(){
		$(".closeModal").click(function(){
			$("#delModal").hide();
		});
	});
	
	
	function deleteBoard(){
		let boardNo = '${requestScope.board.no}';
		
		$.ajax({
	          url: "delBoard.bo", // 데이터가 송수신될 서버의 주소 (서블릿의 매핑주소 작성)
	          type: "get", // 통신 방식 (GET, POST, PUT, DELETE)
	          data : {"boardNo" : boardNo }, // 데이터 보내기
	          dataType: "json", // 수신 받을 데이터 타입 (MINE TYPE)
	          success: function (data) {
	        	  console.log(data);
				$("#delModal").hide();
				
	            // 통신이 성공하면 수행할 함수
	            if (data.status == "success"){
	            	location.href='listAll.bo';
	            } 
	            
	            
	          },
	          error: function () {},
	          complete: function () {
	          },
	        });
		
	}
	
	
	
	function showDeleteModal(){
		$("#delModal").show();
		
	}

	
	
</script>
<style>
	.readLikeCnt{
		display: flex;
		justify-content: space-between;
	}
</style>
</head>
<body>
<c:set var="contextPath" value="<%=request.getContextPath() %>"></c:set>
	<jsp:include page="../header.jsp"></jsp:include>

	${requestScope.board }
	${requestScope.upLoadFile }
	
	<div class="container">
		<h1>게시판 글 조회</h1>
			
			<div class="mb-3 mt-3">
			    <label for="no" class="form-label">글번호:</label>
			    <input type="text" class="form-control" id="no" value="${requestScope.board.no}" readonly>
			</div>
			
			<div class="mb-3 mt-3">
			    <label for="writer" class="form-label">작성자:</label>
			    <input type="text" class="form-control" id="writer" value="${requestScope.board.writer}" readonly>
			</div>
			
			<div class="mb-3 mt-3">
			    <label for="title" class="form-label">제목:</label>
			    <input type="text" class="form-control" id="title"  value= "${requestScope.board.title}" >
			</div>
			
			<div class="readLikeCnt">
				<div class="readCount"> 조회수<span class="badge bg-primary">${requestScope.board.readcount}</span></div>
				<div class="likeCount"> 좋아요<span class="badge bg-info">${requestScope.board.likecount}</span> </div>
			</div>
			
			
			<div class="mb-3 mt-3">
			    <label for="content" class="form-label">내용:</label>
			    <textarea class="form-control" rows="10" style="width: 100%" id="content" >${requestScope.board.content}</textarea>
<%-- 			    <div id="content">${requestScope.board.content}</div> --%>
			</div>
			
			<c:if test="${requestScope.upLoadFile != null }">
				<div class="mb-3 mt-3">
				    <label for="upFile" class="form-label">첨부이미지 :</label>
				    <img src="${contextPath}/${requestScope.upLoadFile.newFileName }" /> 
				</div>
				<div><span>${requestScope.upLoadFile.originalFileName }</span></div>
			</c:if>
			
			
			<c:choose>
				<c:when test="${sessionScope.loginUser != null && sessionScope.loginUser.userId == requestScope.board.writer}">
					<button type="button" class="btn btn-success">수정</button>
					<button type="button" class="btn btn-danger" onclick="showDeleteModal();" >삭제</button>
					<button type="button" class="btn btn-warning" onclick="location.href='replyBoard.jsp?ref=${requestScope.board.ref}&step=${requestScope.board.step }&reforder=${requestScope.board.reforder }'">답글달기</button>
				</c:when>
				<c:when test="${sessionScope.loginUser != null && sessionScope.loginUser.userId != requestScope.board.writer}">
					<button type="button" class="btn btn-success" disabled>수정</button>
					<button type="button" class="btn btn-danger" disabled>삭제</button>
					<button type="button" class="btn btn-warning" onclick="location.href='replyBoard.jsp?ref=${requestScope.board.ref}&step=${requestScope.board.step }&reforder=${requestScope.board.reforder }'">답글달기</button>
				</c:when>
				<c:otherwise>
					<button type="button" class="btn btn-success" disabled>수정</button>
					<button type="button" class="btn btn-danger" disabled>삭제</button>
					<button type="button" class="btn btn-warning" disabled>답글달기</button>
				</c:otherwise>
			</c:choose>	
		
			<button type="button" class="btn btn-info" onclick="location.href='listAll.bo'">목록으로</button>
		
	</div>
	
<!-- 	첨부파일이 있는 경우에만 이미지를 보여준다. (jstl) -->
<!-- 	작성자만 수정과 삭제를 할 수 있다. (jstl) -->
<!-- 답글달기는 로그인해야 달 수 있다.	 -->
	
	<!-- 글 삭제를 위한 모달 : The Modal -->
<div class="modal" id="delModal">
  <div class="modal-dialog">
    <div class="modal-content">

      <!-- Modal Header -->
      <div class="modal-header">
        <h4 class="modal-title">알림</h4>
        <button type="button" class="btn-close closeModal" data-bs-dismiss="modal"></button>
      </div>

      <!-- Modal body -->
      <div class="modal-body">
        ${requestScope.board.no} 번글을 삭제할까요?
      </div>

      <!-- Modal footer -->
      <div class="modal-footer">
        <button type="button" class="btn btn-success" data-bs-dismiss="modal" onclick="deleteBoard();">삭제</button>
        <button type="button" class="btn btn-danger closeModal" data-bs-dismiss="modal">취소</button>
      </div>

    </div>
  </div>
</div>
	
	
	
	
<jsp:include page="../footer.jsp"></jsp:include>
</body>
</html>