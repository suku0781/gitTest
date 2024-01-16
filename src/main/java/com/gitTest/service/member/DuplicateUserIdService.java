package com.gitTest.service.member;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.gitTest.controller.MemberFactory;
import com.gitTest.dao.MemberCRUD;
import com.gitTest.dao.MemberDAO;
import com.gitTest.service.MemberService;
import com.gitTest.vo.Member;

public class DuplicateUserIdService implements MemberService {

	@Override
	public MemberFactory executeService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("아이디 중복 검사 하러 가자");
		
		String tmpUserId = request.getParameter("tmpUserId");
		System.out.println("중복검사할 아이디: " + tmpUserId);
		
		response.setContentType("application/json; charset=utf-8");
		PrintWriter out = response.getWriter();
		
		MemberDAO mdao = MemberCRUD.getInstance();
		
		Map<String, String> jsonMap = new HashMap<String, String>();
		
		try {
			Member mem = mdao.duplicateUserId(tmpUserId);
			
//			System.out.println(mem.toString());
			if (mem != null) {
				// 아이디가 중복이다. ("isDuplicate" : "true")
				jsonMap.put("isDuplicate", "true");
			} else if (mem == null) {
				// 중복 아니다.
				jsonMap.put("isDuplicate", "false");
			}
			
			jsonMap.put("responseCode", "00");
			
			SimpleDateFormat fmt = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초");
			String outputDate = fmt.format(Calendar.getInstance().getTime());
			jsonMap.put("outputDate", outputDate);
			
			
		} catch (NamingException | SQLException e) {
			e.printStackTrace();
			jsonMap.put("responseCode", "err");
			jsonMap.put("errMsg", e.getMessage());
		}
		
		JSONObject json = new JSONObject(jsonMap);
		out.print(json.toJSONString());
		
		out.flush();
		out.close();
		
		
		return null;
	}

}