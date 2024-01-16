package com.gitTest.service.board;

import java.io.IOException;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gitTest.service.BoardService;
import com.gitTest.controller.BoardFactory;
import com.gitTest.dao.BoardCRUD;
import com.gitTest.dao.BoardDAO;
import com.gitTest.etc.UploadedFile;
import com.gitTest.vo.Board;

public class GetEntireBoardService implements BoardService {

	@Override
	public BoardFactory doAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		BoardFactory bf = BoardFactory.getInstance();
		
		String writer = request.getParameter("writer");
		String title = request.getParameter("title");
		String content = request.getParameter("content");
		int ref = Integer.parseInt(request.getParameter("ref"));
		int step = Integer.parseInt(request.getParameter("step"));
		int reforder = Integer.parseInt(request.getParameter("reforder"));
		
		System.out.println("저장되어야 할 답글: ref: " + ref + ", writer : " + writer );
		
		BoardDAO dao = BoardCRUD.getInstance();
		
		Board tmpBoard = new Board(-1, writer, title, null, content, -1, -1, ref, step, reforder, null);
		
		
		try {
			if (dao.insertReplyTransaction(tmpBoard)) {
				// 리다이렉트 -> 게시판 목록
				bf.setRedirect(true);
				bf.setWhereToGo("listAll.bo");
			}
			
		} catch (NamingException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return bf;
		
	}
	
}
