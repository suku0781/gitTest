package com.gitTest.dao;

import java.sql.SQLException;
import java.util.List;

import javax.naming.NamingException;

import com.gitTest.etc.PagingInfo;
import com.gitTest.etc.UploadedFile;
import com.gitTest.vo.Board;
import com.gitTest.vo.SearchCriteria;

public interface BoardDAO {

	// 게시판 전체 글 목록
	List<Board> selectAllBoard() throws NamingException, SQLException;
	
	// 게시판 글 저장 (업로드파일이 있는 경우)
	int insertBoardWithUploadFileTransaction(Board tmpBoard, UploadedFile uf)  throws NamingException, SQLException;
	
	// 게시판 글 저장 (업로드파일이 없는 경우)
	int insertBoardTransaction(Board tmpBoard)  throws NamingException, SQLException;

	// ------ 조회수 처리 -------
	// readcountprocess테이블에 ip주소와 글번호no가 있는지  없는지
	boolean selectReadCountProcess(String userIp, int no)  throws NamingException, SQLException;

	// 24시간이 지났는지 아닌지 (시간차이)
	int selectHourDiff(String userIp, int no)  throws NamingException, SQLException;

	// 아이피 주소와 글번호와 읽은 시간을 readcoundprocess 테이블에 update하거나 insert
	int readCountProcessWithReadCntInc(String userIp, int no, String how) throws NamingException, SQLException;

	// no번 글 가져오기
	Board selectBoardByNo(int no) throws NamingException, SQLException;

	// 게시판 첨부 파일 가져오기
	UploadedFile getFile(int no) throws NamingException, SQLException;

	// boardNo번 글 삭제
	boolean deleteBoard(int boardNo) throws NamingException, SQLException;

	// 답글 처리
	boolean insertReplyTransaction(Board tmpBoard) throws NamingException, SQLException;

	
	// 페이징 처리
	// 총 게시글 수 가져오기
	int getTotalPostCnt()  throws NamingException, SQLException;

	// 페이징 정보에 따라서 게시글 가져오기 
	List<Board> selectAllBoard(PagingInfo pi) throws NamingException, SQLException;

	
	// 검색어 처리
	// 게시글 수 가져오기 (검색어가 있을 때)
	int getTotalPostCnt(SearchCriteria sc)  throws NamingException, SQLException;

	// 게시글 목록 (페이징 처리, 검색어가 있을 때)
	List<Board> selectAllBoard(PagingInfo pi, SearchCriteria sc)  throws NamingException, SQLException;
	
	
	
}
