<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
<title>Insert title here</title>
<script>
let MailValid = false;

$(function(){
	// 아이디 작성을 마쳤을때
	$("#userId").blur(function(){
		validUserId();
	});
	
	// 비밀번호 작성을 마쳤을때
	$("#userPwd2").blur(function(){
		validUserPwd();
	});
	
	// 이메일 인증 버튼 클릭시
	$(".sendMail").click(function(){
		if ($('#userEmail').val() != ''){
			// 이메일 보내고
// 			alert('이메일 보내자');
			$.ajax({
		          url: "sendMail.mem", // 데이터가 송수신될 서버의 주소 (서블릿의 매핑주소 작성)
		          type: "get", // 통신 방식 (GET, POST, PUT, DELETE)
		          data : {
		        	  "tmpUserEmail" : $('#userEmail').val()
		          }, // 데이터 보내기
		          dataType: "json", // 수신 받을 데이터 타입 (MINE TYPE)
		          async: false, // 동기식 
		          success: function (data) {
		        	  console.log(data);
		        	  if (data.status == 'success'){
			        	  alert('메일을 발송했습니다');
		        	  } else if (data.status == 'fail'){
		        		  alert('메일 발송 실패!');
		        	  }
		          },
		          error: function () {},
		          complete: function () {
		          },
		        });
			
			
			$('.codeDiv').show();
			
		} else {
			alert('이메일 주소를 입력하고 인증버튼을 누르세요');
			$('#userEmail').focus();
		}
	});
	
	
	// 코드확인 버튼 클릭시
	$('.confirmCode').click(function(){
		$.ajax({
	          url: "confirmCode.mem", // 데이터가 송수신될 서버의 주소 (서블릿의 매핑주소 작성)
	          type: "get", // 통신 방식 (GET, POST, PUT, DELETE)
	          data : {
	        	  "tmpMailCode" : $('#mailcode').val()
	          }, // 데이터 보내기
	          dataType: "json", // 수신 받을 데이터 타입 (MINE TYPE)
	          async: false, // 동기식 
	          success: function (data) {
	        	  console.log(data);
	        	  if (data.activation == "success"){
	        		  alert("인증성공");
	        		  MailValid = true;
	        	  } else {
	        		  alert("인증 실패");
	        	  }
	          },
	          error: function () {},
	          complete: function () {
	          },
	        });
		
		
		
	});
	
	
}); // end of doc





function validUserId(){
	// 아이디 유효성 검사 
	// 3자 이상 ~ 8자 이하
	let isValid = false;
	console.log(isValid);
	let tmpUserId = $("#userId").val();
// 	alert(userId);
	if (tmpUserId.length > 2 && tmpUserId.length < 9){
		// 아이디 중복검사
// 		alert(tmpUserId);
		$.ajax({
	          url: "duplicateUserId.mem", // 데이터가 송수신될 서버의 주소 (서블릿의 매핑주소 작성)
	          type: "get", // 통신 방식 (GET, POST, PUT, DELETE)
	          data : {
	        	  "tmpUserId" : tmpUserId
	          }, // 데이터 보내기
	          dataType: "json", // 수신 받을 데이터 타입 (MINE TYPE)
	          async: false, // 동기식 
	          success: function (data) {
	        	  console.log(data);
	        	  console.log(isValid);
// 	            // 통신이 성공하면 수행할 함수
					if (data.responseCode != "00"){
						alert("DB에 문제가 있습니다. 다시 시도해 주세요");
					} else {
						if (data.isDuplicate == "true"){
							// 아이디 중복
							printErrMsg("userId", "아이디가 중복됩니다!", true);
						} else if (data.isDuplicate == "false"){
							// 사용가능한 아이디
							printErrMsg("userId", "사용 가능한 아이디입니다!", false);
							isValid = true;
						}
							console.log(isValid);
					}
	
	          },
	          error: function () {},
	          complete: function () {
	          },
	        });
		
	} else {
		printErrMsg('userId', '아이디는 3자 이상 8자 이하로 필수입니다.', true);
	}
	
	return isValid;	
	
}

function validUserPwd(){
	let isValid = false;
	
	// pwd1과 pwd2가 일치하는지 검사
	if($('#userPwd').val() != $('#userPwd2').val()){
		$('#userPwd').val('');
		$('#userPwd2').val('');
		printErrMsg('userPwd', '비밀번호가 일치하지 않습니다', true);
	} else {
		isValid = true;
	}
	
	return isValid;
}

// 회원가입 버튼을 눌렀을 때
function validCheck(){
	let isValid = false;
	
	let userIdValid = validUserId();
	let pwdValid = validUserPwd();
	// 이메일 통과여부 (추가해야할 부분) : MailValid (전역변수)
	
	let checkAgree = $("input[type=checkbox][name=agree]:checked").val();
	
	if (checkAgree == undefined) {
		printErrMsg('agree', '가입조항을 체크해 주세요', false);
	}
// 	alert(checkAgree);
	console.log(userIdValid, pwdValid, MailValid, checkAgree );
	
	if (userIdValid && pwdValid && MailValid && checkAgree == 'Y'){
		isValid = true;
	}
	
	return isValid;
}


function printErrMsg(id, msg, isFocus){
	let errMsg = `<div class="errMsg">\${msg}</div>`;
	$(errMsg).insertAfter($(`#\${id}`));
	if (isFocus){
		$(`#\${id}`).focus();
	}
	
	$('.errMsg').hide(2000);
}

</script>
<style>
	.errMsg {
		color : red;
		font-size: 14px;
		font-weight: bold;	
	}
</style>
</head>
<body>
<jsp:include page="../header.jsp"></jsp:include>
	<div class="container">
		<h1>회원가입</h1>
		<form action="registerMember.mem" method="post" enctype="multipart/form-data">
			<div class="mb-3 mt-3">
			    <label for="userId" class="form-label">아이디:</label>
			    <input type="text" class="form-control" id="userId" placeholder="Enter your id" name="userId">
			</div>
			
			<div class="mb-3 mt-3">
			    <label for="userPwd" class="form-label">비밀번호:</label>
			    <input type="password" class="form-control" id="userPwd" placeholder="Enter your password" name="userPwd">
			</div>
			<div class="mb-3 mt-3">
			    <label for="userPwd2" class="form-label">비밀번호 확인:</label>
			    <input type="password" class="form-control" id="userPwd2" placeholder="Enter your password again" >
			</div>
			
			<div class="mb-3 mt-3">
			    <label for="userEmail" class="form-label">이메일:</label>
			    <input type="text" class="form-control" id="userEmail" placeholder="Enter your email" name="userEmail">
			    <button type="button" class="btn btn-warning sendMail" >이메일인증</button>
			    
			    <div class='codeDiv' style="display: none;">
			    	<input type="text" class="form-control" id="mailcode" placeholder="인증코드를 입력하세요" />
			    	<button type="button" class="btn btn-success confirmCode">코드확인</button>
			    </div>
			    
			</div>
		
			
			<div class="mb-3 mt-3">
			    <label for="userImg" class="form-label">이미지:</label>
			    <input type="file" class="form-control" id="userImg" placeholder="Enter your email" name="userImg">
			</div>
		
			<div class="mb-3 mt-3" id="agree">
			   <input type="checkbox" class="form-check-input"  name="agree" value="Y">
      			<label class="form-check-label" for="check1">가입조항에 동의합니다.</label>
			</div>
			
			<button type="submit" class="btn btn-success" onclick="return validCheck();">회원가입</button>
			<button type="reset" class="btn btn-danger">취소</button>
		
		</form>
		
		
		
	</div>
<jsp:include page="../footer.jsp"></jsp:include>
</body>
</html>