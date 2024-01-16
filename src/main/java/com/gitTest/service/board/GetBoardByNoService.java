package com.gitTest.service.board;

import java.io.IOException;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gitTest.controller.BoardFactory;
import com.gitTest.dao.BoardCRUD;
import com.gitTest.dao.BoardDAO;
import com.gitTest.etc.UploadedFile;
import com.gitTest.service.BoardService;
import com.gitTest.vo.Board;

public class GetBoardByNoService implements BoardService {

	@Override
	public BoardFactory doAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int no = Integer.parseInt(request.getParameter("no"));
		
		System.out.println("상세조회할 게시판 글번호 : " + no);
		
		// 클라이언트 ip주소 얻어오기
		String userIp = getIp(request);
		
		BoardDAO dao = BoardCRUD.getInstance();
		
		int result = -1;
		 
		try {
			if (dao.selectReadCountProcess(userIp, no)) { //해당 아이피 주소와 글번호 같은 것이 있으면...
				
				if (dao.selectHourDiff(userIp, no) > 23) { // 시간이 24시간이 지난 경우
//				->아이피 주소와 글번호와 읽은 시간을 readcoundprocess 테이블에서 update
//				-> 해당 글번호의 readcount를 증가 (update)
					result = dao.readCountProcessWithReadCntInc(userIp, no, "update");
				}
				
			} else { // 해당 아이피 주소와 글번호 같은 것이 없으면 (글을 최초로 조회)
				 result = dao.readCountProcessWithReadCntInc(userIp, no, "insert");
//			-> 아이피 주소와 글번호와 읽은 시간을 readcoundprocess 테이블에 insert
//			-> 해당 글번호의 readcount를 증가 (update)
			}
			
			//  해당 글을 가져옴 (select)
			Board board = dao.selectBoardByNo(no);
			UploadedFile attachedFile = dao.getFile(no);
						
			if (board != null) {
				request.setAttribute("board", board);
				request.setAttribute("upLoadFile", attachedFile);
				
				request.getRequestDispatcher("viewBoard.jsp").forward(request, response);
				
				// viewBoard.jsp
			}
			
			
		} catch (NamingException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
//			e.getMessage(); // String으로 반환
//			e.getStackTrace(); // array 으로 반환
			
			request.setAttribute("errorMsg", e.getMessage());
			request.setAttribute("errorStack", e.getStackTrace());
			
			request.getRequestDispatcher("../commonError.jsp").forward(request, response);
			
		}
		
		return null;
	}
	
	private String getIp(HttpServletRequest request) {
		
		String ip = request.getHeader("X-Forwarded-For");
		
		System.out.println(">>>> X-FORWARDED-FOR : " + ip );
		
		if ( ip == null) {
			ip = request.getHeader("Proxy-Client-IP");
			System.out.println(">>>> Proxy-Client-IP : " + ip);
		}
		
		if ( ip == null) {
			ip = request.getHeader("WL-Proxy-Client-IP");
			System.out.println(">>>> WL-Proxy-Client-IP : " + ip);
		}
		
		if ( ip == null) {
			ip = request.getHeader("HTTP_CLIENT_IP");
			System.out.println(">>>> HTTP_CLIENT_IP : " + ip);
		}
		
		if ( ip == null) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
			System.out.println(">>>> HTTP_X_FORWARDED_FOR : " + ip);
		}
		
		if (ip == null) {
			ip = request.getRemoteAddr();
		}
		
		System.out.println(">>>> Result : IP Address : " + ip);
		
		return ip;
	}

}
