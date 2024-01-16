package com.gitTest.service.member;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.gitTest.controller.MemberFactory;
import com.gitTest.service.MemberService;

public class ConfirmMailCodeService implements MemberService {

	@Override
	public MemberFactory executeService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userInputMailCode = request.getParameter("tmpMailCode"); // 유저가 입력한 인증코드
		String code = (String)request.getSession().getAttribute("authCode"); // 우리가 세션에 저장해 놓은 인증코드
		
		System.out.println(" userInputMailCode: " + userInputMailCode);
		System.out.println(" code: " + code);
		
		response.setContentType("application/json; charset=utf-8;");
		PrintWriter out = response.getWriter();
		
		JSONObject json = new JSONObject();
		
		if (userInputMailCode.equals(code)) {
			json.put("activation", "success");
		} else {
			json.put("activation", "fail");
		}
		
		out.print(json.toJSONString());
		out.flush();
		out.close();
		
		return null;
	}

}
