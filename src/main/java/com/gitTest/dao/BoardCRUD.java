package com.gitTest.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import com.gitTest.etc.PagingInfo;
import com.gitTest.etc.UploadedFile;
import com.gitTest.vo.Board;
import com.gitTest.vo.SearchCriteria;

public class BoardCRUD implements BoardDAO {

	private static BoardCRUD instance = null;
	private BoardCRUD() {}
	public static BoardCRUD getInstance() {
		if (instance == null) {
			instance = new BoardCRUD();
		}
		return instance;
	}
	
	@Override
	public List<Board> selectAllBoard() throws NamingException, SQLException {
		
		List<Board> lst = new ArrayList<Board>();
		
		Connection con = DBConnection.getInstance().dbConnect();
		
		String query = "select * from board order by ref desc, reforder asc";
		
		PreparedStatement pstmt = con.prepareStatement(query);
		
		ResultSet rs = pstmt.executeQuery();
		
		while(rs.next()) {
			lst.add(new Board(rs.getInt("no"), 
					rs.getString("writer"), 
					rs.getString("title"), 
					rs.getTimestamp("postDate"), 
					rs.getString("content"), 
					rs.getInt("readcount"), 
					rs.getInt("likecount"), 
					rs.getInt("ref"), 
					rs.getInt("step"), 
					rs.getInt("reforder"), 
					rs.getString("isDelete")));
		}
		
		DBConnection.getInstance().dbClose(rs, pstmt, con);
		
		return lst;

	}
	
	// ------------ 게시판 글 작성 --------------------
	@Override
	public int insertBoardWithUploadFileTransaction(Board tmpBoard, UploadedFile uf)
			throws NamingException, SQLException {

	// 업로드파일이 있는 경우
	//(1) 게시글정보를 board테이블에 insert	
	//(2) uploadedfile테이블에 업로드파일정보를 insert (board테이블의 no값을 boardNo 값으로 추가)
	//(3) "게시물작성"에 대한 포인트 부여
	//(4) pointlog에 기록 	
		
		int result = -1;
		Connection con = DBConnection.getInstance().dbConnect();
		con.setAutoCommit(false);
		
		if (insertBoard(tmpBoard, con)) { // (1) 게시글정보를 board테이블에 insert	
			uf.setBoardNo(tmpBoard.getNo());
			
			if (insertUploadFile(uf, con)) { // (2) uploadedfile테이블에 업로드파일정보를 insert 
				// (3) + (4) 
				if (MemberCRUD.getInstance().addPointToMember(tmpBoard.getWriter(), "게시물작성", 2, con)) {
					result = 0;
					con.commit();
				} else {
					con.rollback();
				}
				
			} else {
				con.rollback();
			}
		} else {
			con.rollback();
		}
		
		con.setAutoCommit(true);
		con.close();
		
		return result;
	}
	
	
	
	private boolean insertUploadFile(UploadedFile uf, Connection con) throws SQLException {
		// (2) uploadedfile테이블에 업로드파일정보를 insert
		// (board테이블의 no값을 boardNo 값으로 추가)
		
		boolean result = false;
		
		String query ="insert into uploadedfile(originalFileName, ext, newFileName, fileSize, boardNo, base64String) values(?,?,?,?,?,?)";
		
		PreparedStatement pstmt = con.prepareStatement(query);
		
		pstmt.setString(1, uf.getOriginalFileName());
		pstmt.setString(2, uf.getExt());
		pstmt.setString(3, uf.getNewFileName());
		pstmt.setLong(4, uf.getSize());
		pstmt.setInt(5, uf.getBoardNo());
		pstmt.setString(6, uf.getBase64String());
		
		if (pstmt.executeUpdate() == 1) {
			result = true;
		}
		
		pstmt.close();
		
		return result;
	}
	
	private boolean insertBoard(Board tmpBoard, Connection con) throws SQLException {
		// 결과를 반환할 변수
		boolean result = false;
		
		String query = "insert into board(writer, title, content, ref) "
				+ " value(?, ?, ?, ?)";
		
		int nextRef = getNextRef(con);
		System.out.println("nextRef : " + nextRef);
		
		PreparedStatement pstmt = con.prepareStatement(query);
		
		pstmt.setString(1, tmpBoard.getWriter());
		pstmt.setString(2, tmpBoard.getTitle());
		pstmt.setString(3, tmpBoard.getContent());
		pstmt.setInt(4, nextRef);
		
		tmpBoard.setNo(nextRef);
		
		if (pstmt.executeUpdate() == 1) {
			result = true;
		}
		System.out.println("insertBoard 결과 : " + result);
		pstmt.close();
		
		return result;
		
	}
	
	private int getNextRef(Connection con) throws SQLException {
		// 다음 저장될 게시글의 no값이자 ref값
		int nextRef = -1;
		
		String query = "select max(no) + 1 as nextref from board";
//		String query = "select auto_increment as nextref from information_schema.tables "
//				+ "where table_schema = 'ksy' and table_name = 'board'";
		PreparedStatement pstmt = con.prepareStatement(query);
		
		ResultSet rs = pstmt.executeQuery();
		
		while(rs.next()) {
			nextRef = rs.getInt("nextref");
		}
		
		rs.close();
		pstmt.close();
		
		return nextRef;
	}
	
	
	
	@Override
	public int insertBoardTransaction(Board tmpBoard) throws NamingException, SQLException {
		// 업로드파일이 없는 경우
		int result = -1;
		
		Connection con = DBConnection.getInstance().dbConnect();
		con.setAutoCommit(false);
		
		if (insertBoard(tmpBoard, con)) {
			if (MemberCRUD.getInstance().addPointToMember(tmpBoard.getWriter(), "게시물작성", 2, con)) {
				result = 0;
				con.commit();
			} else {
				con.rollback();
			}
		} else {
			con.rollback();
		}
		
		con.setAutoCommit(true);
		con.close();
		
		return result;
		
	}
	
	// ------------------------ 조회수 처리 --------------------------
	@Override
	public boolean selectReadCountProcess(String userIp, int no) throws NamingException, SQLException {
		// 해당 아이피 주소와 글번호 같은 것이 있는지 없는지
		boolean result = false;
		
		Connection con = DBConnection.getInstance().dbConnect();
		
		String query = "select * from readcountprocess where boardNo = ? and ipAddr = ?";
		
		PreparedStatement pstmt = con.prepareStatement(query);
		pstmt.setInt(1, no);
		pstmt.setString(2, userIp);
		
		ResultSet rs = pstmt.executeQuery();
		
		while(rs.next()) {
			result = true;
		}
		
		DBConnection.getInstance().dbClose(rs, pstmt, con);
		
		return result;
	}
	
	
	@Override
	public int selectHourDiff(String userIp, int no) throws NamingException, SQLException {
		int result = -1; // 시간차이
		
		Connection con = DBConnection.getInstance().dbConnect();
		
		String query = "select timestampdiff(hour, "
		+ "(select readTime from readcountprocess where ipAddr = ? and boardNo = ?), now()) as hourDiff";
		
		PreparedStatement pstmt = con.prepareStatement(query);
		pstmt.setString(1, userIp);
		pstmt.setInt(2, no);
		
		ResultSet rs = pstmt.executeQuery();
		
		while(rs.next()) {
			result = rs.getInt("hourDiff");
		}
		
		DBConnection.getInstance().dbClose(rs, pstmt, con);
		
		return result;
	}
	
	@Override
	public int readCountProcessWithReadCntInc(String userIp, int no, String how) throws NamingException, SQLException {
		// 아이피 주소와 글번호에 대해 읽은 시간을 readcoundprocess 테이블에서 update 
		// 해당 글번호의 readcount를 증가 (update)
		
		// or 아이피 주소와 글번호와 읽은 시간을 readcoundprocess 테이블에 insert
		// 해당 글번호의 readcount를 증가 (update)
		
		
		int result = -1;
		
		Connection con = DBConnection.getInstance().dbConnect();
		con.setAutoCommit(false);
		
		String query = "";
		PreparedStatement pstmt = null;
		
		if (how.equals("update") ) {
			query = "update readcountprocess set readTime = now() "
					+ "where ipAddr = ? and boardNo = ?";
			
		} else if (how.equals("insert")) {
			query = "insert into readcountprocess (ipAddr, boardNo) "
					+ "values (?, ?)";
		}
		
		pstmt = con.prepareStatement(query);
		pstmt.setString(1, userIp);
		pstmt.setInt(2, no);
		
		if (pstmt.executeUpdate() == 1) {
			
			if (updateReadCount(no, con)) { // 조회수 증가
				result = 0;
				con.commit();
			} else {
				con.rollback();
			}
		} else {
			con.rollback();
		}
		
		con.setAutoCommit(true);
		con.close();
		
		return result;
		
	}
	
	
	private boolean updateReadCount(int no, Connection con) throws SQLException {
		
		boolean result = false;
		
		String query = "update board set readcount = readcount + 1 where no = ?";
		
		PreparedStatement pstmt = con.prepareStatement(query);
		pstmt.setInt(1, no);
		
		if (pstmt.executeUpdate() == 1) {
			result = true;
		}
		
		pstmt.close();
		
		return result;
	}
	
	@Override
	public Board selectBoardByNo(int no) throws NamingException, SQLException {

		Board board = null;
		
		Connection con = DBConnection.getInstance().dbConnect();
		
		String query = "select * from board where no = ?";
		
		PreparedStatement pstmt = con.prepareStatement(query);
		pstmt.setInt(1, no);
		
		ResultSet rs = pstmt.executeQuery();
		
		while (rs.next()) {
			board = new Board(rs.getInt("no"), 
					rs.getString("writer"), 
					rs.getString("title"), 
					rs.getTimestamp("postDate"), 
					rs.getString("content"), 
					rs.getInt("readcount"), 
					rs.getInt("likecount"), 
					rs.getInt("ref"), 
					rs.getInt("step"), 
					rs.getInt("reforder"), 
					rs.getString("isDelete"));
		}
		
		DBConnection.getInstance().dbClose(rs, pstmt, con);
		
		return board;
		
	}
	@Override
	public UploadedFile getFile(int no) throws NamingException, SQLException {
		
		UploadedFile uf = null;
		
		Connection con = DBConnection.getInstance().dbConnect();
		
		String query = "select * from uploadedfile where boardNo = ?";
		
		PreparedStatement pstmt = con.prepareStatement(query);
		pstmt.setInt(1, no);
		
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			uf = new UploadedFile(rs.getString("originalFileName"), 
					rs.getString("ext"), 
					rs.getString("newFileName"), 
					rs.getLong("fileSize"), 
					rs.getInt("boardNo"), 
					rs.getString("base64String"));
		}
		
		DBConnection.getInstance().dbClose(rs, pstmt, con);
		
		return uf;
	}
	@Override
	public boolean deleteBoard(int boardNo) throws NamingException, SQLException {
		// 글 삭제
		boolean result = false;
		
		Connection con = DBConnection.getInstance().dbConnect();
		
		String query = "update board set isDelete = 'Y' where no = ?";
		
		PreparedStatement pstmt = con.prepareStatement(query);
		pstmt.setInt(1, boardNo);
		
		if (pstmt.executeUpdate() == 1) {
			result = true;
		}
		
		DBConnection.getInstance().dbClose(pstmt, con);
		
		return result;
	}
	
	
	@Override
	public boolean insertReplyTransaction(Board tmpBoard) throws NamingException, SQLException {
		// 답글 처리
		boolean result = false;
		
		Connection con = DBConnection.getInstance().dbConnect();
		con.setAutoCommit(false);
		
		int updateResult = updateRefOrder(tmpBoard, con);
		
		if (updateResult >= 0) {
			if (insertReply(tmpBoard, con) == 1) {
				if (MemberCRUD.getInstance().addPointToMember(tmpBoard.getWriter(), "답글작성", 1, con)) {
					result = true;
					con.commit();
				} else {
					con.rollback();
				}
			} else {
				con.rollback();
			}
		} else {
			con.rollback();
		}
		
		con.setAutoCommit(true);
		con.close();
		
		return result;
		
	}
	
	private int insertReply(Board board, Connection con) throws SQLException {
		
		int result = -1;
		String query = "insert into board(writer, title, content, ref,  step, reforder) "
				+ "values( ?, ?, ?, ?, ?, ?)";
		
		PreparedStatement pstmt = con.prepareStatement(query);
		pstmt.setString(1, board.getWriter());
		pstmt.setString(2, board.getTitle());
		pstmt.setString(3, board.getContent());
		pstmt.setInt(4, board.getRef());
		pstmt.setInt(5, board.getStep() + 1);
		pstmt.setInt(6, board.getReforder() + 1);
		
		result = pstmt.executeUpdate();
		pstmt.close();
		
		return result;
	}
	
	private int updateRefOrder(Board board, Connection con) throws SQLException {
		
		int result = -1;
		
		String query = "update board set reforder = reforder + 1 "
				+ "where ref = ? and reforder > ? ";
		
		PreparedStatement pstmt = con.prepareStatement(query);
		
		pstmt.setInt(1, board.getRef());
		pstmt.setInt(2, board.getReforder());
		
		result = pstmt.executeUpdate();
		pstmt.close();
		
		return result;
	}
	
	
	@Override
	public int getTotalPostCnt() throws NamingException, SQLException {
		
		int result = -1;
		
		Connection con = DBConnection.getInstance().dbConnect();
		String query = "select count(*) as totalPostCnt from board";
		
		PreparedStatement pstmt = con.prepareStatement(query);
		ResultSet rs = pstmt.executeQuery();
		
		while(rs.next()) {
			result = rs.getInt("totalPostCnt");
		}
		
		DBConnection.getInstance().dbClose(rs, pstmt, con);
		
		return result;
		
	}
	
	@Override
	public List<Board> selectAllBoard(PagingInfo pi) throws NamingException, SQLException {
		
		List<Board> lst = new ArrayList<Board>();
		
		Connection con = DBConnection.getInstance().dbConnect();
		
		String query = "select * from board order by ref desc, reforder asc limit ?, ?";
		
		PreparedStatement pstmt = con.prepareStatement(query);
		pstmt.setInt(1, pi.getStartRowIndex());
		pstmt.setInt(2, pi.getViewPostCntPerPage());
		
		ResultSet rs = pstmt.executeQuery();
		
		while(rs.next()) {
			lst.add(new Board(rs.getInt("no"), 
					rs.getString("writer"), 
					rs.getString("title"), 
					rs.getTimestamp("postDate"), 
					rs.getString("content"), 
					rs.getInt("readcount"), 
					rs.getInt("likecount"), 
					rs.getInt("ref"), 
					rs.getInt("step"), 
					rs.getInt("reforder"), 
					rs.getString("isDelete")));
		}
		
		DBConnection.getInstance().dbClose(rs, pstmt, con);
		
		return lst;
	}
	@Override
	public int getTotalPostCnt(SearchCriteria sc) throws NamingException, SQLException {
		// 총 게시글 수 가져오기 (검색어가 있을 때)
		int result = -1;
		
		Connection con = DBConnection.getInstance().dbConnect();
		
		String query ="select count(*) as totalPostCnt from board where ";
		
		if (sc.getSearchType().equals("writer")) {
			query += "  writer like ?";
		} else if (sc.getSearchType().equals("title")) {
			query += "  title like ?";
		} else if (sc.getSearchType().equals("content")) {
			query += "  content like ?";
		}
		
		PreparedStatement pstmt = con.prepareStatement(query);
		pstmt.setString(1, "%" + sc.getSearchWord() + "%");
		
		ResultSet rs = pstmt.executeQuery();
		
		while(rs.next()) {
			result = rs.getInt("totalPostCnt");
		}
			
		DBConnection.getInstance().dbClose(rs, pstmt, con);
		
		return result;
	}
	@Override
	public List<Board> selectAllBoard(PagingInfo pi, SearchCriteria sc) throws NamingException, SQLException {
		// 게시글 목록 (페이징 처리, 검색어가 있을 때)
		List<Board> lst = new ArrayList<Board>();
		
		Connection con = DBConnection.getInstance().dbConnect();
		String query = "select * from board where ";
		
		if (sc.getSearchType().equals("writer")) {
			query += "writer like ? order by ref desc, reforder asc limit ?, ?";
		} else if (sc.getSearchType().equals("title")) {
			query += "title like ? order by ref desc, reforder asc limit ?, ?";
		} else if (sc.getSearchType().equals("content")) {
			query += "content like ? order by ref desc, reforder asc limit ?, ?";
		}
		
		PreparedStatement pstmt = con.prepareStatement(query);
		pstmt.setString(1, "%" + sc.getSearchWord() + "%");
		pstmt.setInt(2, pi.getStartRowIndex());
		pstmt.setInt(3, pi.getViewPostCntPerPage());
		
		ResultSet rs = pstmt.executeQuery();
		while(rs.next()) {
			lst.add(new Board(rs.getInt("no"), 
					rs.getString("writer"), 
					rs.getString("title"), 
					rs.getTimestamp("postDate"), 
					rs.getString("content"), 
					rs.getInt("readcount"), 
					rs.getInt("likecount"), 
					rs.getInt("ref"), 
					rs.getInt("step"), 
					rs.getInt("reforder"), 
					rs.getString("isDelete")));
		}
		
		DBConnection.getInstance().dbClose(rs, pstmt, con);
		return lst;
	}

}
