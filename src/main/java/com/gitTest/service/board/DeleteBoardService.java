package com.gitTest.service.board;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.gitTest.service.BoardService;
import com.gitTest.controller.BoardFactory;
import com.gitTest.dao.BoardCRUD;
import com.gitTest.dao.BoardDAO;

public class DeleteBoardService implements BoardService {

	@Override
	public BoardFactory doAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int boardNo = Integer.parseInt(request.getParameter("boardNo"));
		
		System.out.println(boardNo + "번 글 삭제하러가자");
		
		response.setContentType("application/json; charset=utf-8");
		PrintWriter out = response.getWriter();
		
		
		BoardDAO dao = BoardCRUD.getInstance();
		JSONObject json = new JSONObject();
		
		try {
			if (dao.deleteBoard(boardNo)) {
				// json으로 응답
				json.put("status", "success");
				
			}
		} catch (NamingException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
				json.put("status", "fail");
				json.put("errMsg", e.getMessage());
		}
		
		out.print(json.toJSONString());
		
		out.flush();
		out.close();
		
		
		return null;
	}
	

}
