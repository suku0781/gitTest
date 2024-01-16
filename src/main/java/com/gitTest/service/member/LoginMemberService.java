package com.gitTest.service.member;

import java.io.IOException;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.gitTest.controller.MemberFactory;
import com.gitTest.dao.MemberCRUD;
import com.gitTest.dao.MemberDAO;
import com.gitTest.service.MemberService;
import com.gitTest.vo.Member;

public class LoginMemberService implements MemberService {

	@Override
	public MemberFactory executeService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String userId = request.getParameter("userId");
		String userPwd = request.getParameter("userPwd");
		
		System.out.println("로그인하러 가쟈~");
		System.out.println(userId + ", " + userPwd);
		
		MemberFactory mf = MemberFactory.getInstance();
		
		MemberDAO dao = MemberCRUD.getInstance();
		
		int result = -1;
		try {
			Member loginMember =  dao.loginMember(userId, userPwd);
			
			if (loginMember != null) { // 로그인 성공
				System.out.println(loginMember.toString());
				
				// member테이블에 포인트를 update하고, pointlog에 기록 남기기
				result = dao.addPointToMember(userId, "로그인", 5);
				
				loginMember.setUserPoint(loginMember.getUserPoint() + 5);
				System.out.println("로그인 트랜잭션 결과: " + result);
				
				HttpSession ses = request.getSession();
				ses.setAttribute("loginUser", loginMember); // 세션이 로그인유저정보 바인딩
				
//				request.getRequestDispatcher("../index.jsp").forward(request, response);
				mf.setRedirect(true);
				mf.setWhereToGo(request.getContextPath() + "/index.jsp");
				
			} else { // 로그인 실패
				mf.setRedirect(true);
				mf.setWhereToGo("login.jsp?statusfail");
				
			}
			
		} catch (NamingException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
		return mf;
	}

}