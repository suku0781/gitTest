package com.gitTest.service.board;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gitTest.controller.BoardFactory;
import com.gitTest.dao.BoardCRUD;
import com.gitTest.dao.BoardDAO;
import com.gitTest.etc.PagingInfo;
import com.gitTest.service.BoardService;
import com.gitTest.vo.Board;
import com.gitTest.vo.SearchCriteria;

public class GetEntireBoardService implements BoardService {

	@Override
	public BoardFactory doAction(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
	BoardDAO dao = BoardCRUD.getInstance();
	
	// page에 대한 정보가 없으면 1페이지로 주고, 있으면 그 페이지 번호를 대입
	int pageNo = 1;
	
//	 System.out.println("pageNo : "  + pageNo);
	
	 if (request.getParameter("pageNo") != null && !request.getParameter("pageNo").equals("")) {
		 pageNo = Integer.parseInt(request.getParameter("pageNo"));
	 }
	
	 System.out.println( pageNo + "페이지 글목록을 가져오자");
	 
	 // 검색유형과 검색어 추가
	 System.out.println(request.getParameter("searchType"));
	 System.out.println(request.getParameter("searchWord"));
	 
	 String searchType = "";
	 String searchWord = "";
	 
	 if (request.getParameter("searchWord") != null &&  
			 !request.getParameter("searchWord").equals("")) { // 검색어가 있는 경우
		 searchWord = request.getParameter("searchWord");
	 }
	 
	 if (request.getParameter("searchType") != null && 
			 !request.getParameter("searchType").equals("")) {
		 searchType = request.getParameter("searchType");
	 }
	 
	 SearchCriteria sc = new SearchCriteria(searchWord, searchType); // dto 객체 생성
	 
	 System.out.println(sc.toString());
	 
	
	try {
		 PagingInfo pi = pagingProcess(pageNo, sc);
		
		 
		List<Board> lst = null;
		
		if (sc.getSearchWord().equals("")) {
			lst = dao.selectAllBoard(pi); // 검색어가 없는 경우
		} else if (!sc.getSearchWord().equals("") && !sc.getSearchType().equals("")) {
			// 검색어가 있는 경우
			lst = dao.selectAllBoard(pi, sc);
		}
		
		
		System.out.println(lst);
		
		if (lst.size() == 0) {
			request.setAttribute("boardList", null);
		} else {
			request.setAttribute("boardList", lst);
			request.setAttribute("pagingInfo", pi);
		}
		
		request.getRequestDispatcher("listAll.jsp").forward(request, response);
		
		
	} catch (NamingException | SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	return null;
}

private PagingInfo pagingProcess(int pageNo, SearchCriteria sc) throws NamingException, SQLException {
	// 페이징을 처리하기 위한 메서드
	BoardDAO dao = BoardCRUD.getInstance();
	
	PagingInfo pi = new PagingInfo();
	pi.setPageNo(pageNo);
	
	// 검색어가 있으면 검색된 글의 갯수
	
	if (sc.getSearchWord().equals("")) { // 검색어가 없을 때
		pi.setTotalPostCnt(dao.getTotalPostCnt()); // 전체 게시글의 갯수 (검색어가 없을 때)
		
	} else if (!sc.getSearchWord().equals("") && !sc.getSearchType().equals("")) {
		// 검색어가 있을 때
		pi.setTotalPostCnt(dao.getTotalPostCnt(sc));
	}
	

	// 총 페이지 수
	pi.setTotalPageCnt(pi.getTotalPostCnt(), pi.getViewPostCntPerPage());
	
	// 보여주기 시작할 글 index번호
	pi.setStartRowIndex();
	
	
	// 전체 페이징 블럭 갯수
	pi.setTotalPagingBlockCnt();
	
	// 현재 페이지가 속한 페이징 블럭 번호
	pi.setPageBlockOfCurrentPage();
	
	// 현재 페이징 블럭 시작 페이지 번호
	pi.setStartNumOfCurrentPagingBlock();
	
	//  현재 페이징 블럭 끝 페이지 번호
	pi.setEndNumOfCurrentPagingBlock();
	
	
	return pi;
}
	
}
