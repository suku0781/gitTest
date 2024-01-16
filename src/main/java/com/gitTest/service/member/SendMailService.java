package com.gitTest.service.member;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.gitTest.controller.MemberFactory;
import com.gitTest.etc.SendMail;
import com.gitTest.service.MemberService;

public class SendMailService implements MemberService {

	@Override
	public MemberFactory executeService(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String userMailAddr = request.getParameter("tmpUserEmail");
		
		response.setContentType("application/json; charset=utf-8;");
		PrintWriter out = response.getWriter();
		Map<String, String> jsonMap = new HashMap<String, String>();
		
		// 인증코드를 만들고 코드를 세션에 저장 
		String code = UUID.randomUUID().toString();
		
		System.out.println(userMailAddr + "로 메일을 보내자.   "  + code);
		
		request.getSession().setAttribute("authCode", code);

		// 유저이메일로 인증코드를 발송
		try {
			SendMail.sendMail(userMailAddr, code);
			jsonMap.put("status", "success");
			
			
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			jsonMap.put("status", "fail");
		}
		
		JSONObject json = new JSONObject(jsonMap);
		out.print(json.toJSONString());
		
		out.flush();
		out.close();
		
		return null;
	}

}
