package com.gitTest.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import com.gitTest.etc.UploadedFile;
import com.gitTest.vo.Member;
import com.gitTest.vo.PointLog;

public class MemberCRUD implements MemberDAO {

	private static MemberCRUD instance = null;
	private MemberCRUD() { }
	
	public static MemberCRUD getInstance() {
		if (instance == null) {
			instance = new MemberCRUD();
		}
		return instance;
	}
	
	@Override
	public Member duplicateUserId(String tmpUserId) throws NamingException, SQLException {
		
		Member result = null;
		
		Connection con = DBConnection.getInstance().dbConnect();
		
		String query = "select * from member where userId = ?";
		
		PreparedStatement pstmt = con.prepareStatement(query);
		pstmt.setString(1, tmpUserId);
		
		ResultSet rs = pstmt.executeQuery();
		
		while(rs.next()) {
			result = new Member(rs.getString("userId"), 
					rs.getString("userPwd"),
					rs.getString("userEmail"),
					rs.getDate("registerDate"),
					rs.getInt("userImg"),
					rs.getInt("userPoint"));
		}
		
		DBConnection.getInstance().dbClose(rs, pstmt, con);
		
		return result;
	}

	@Override
	public int registerMemberWithFile(UploadedFile uf, Member member, String why, int howmuch)
			throws NamingException, SQLException {
		
		System.out.println(member.toString());
		
		int result = -1;
		Connection con = DBConnection.getInstance().dbConnect();
		con.setAutoCommit(false); // 트랜잭션 처리를 하겠다. 
		
//		(1) 업로드된 파일의 정보를 uploadedfile테이블에 insert
		int no = -1;
		int insertCnt = -1;
		no = insertUploadedFileInfo(uf, con); 
		
//		(2) 회원 가입 (순수한 회원 데이터 저장 + (1)번에서 저장된 no (pk)를 userImg에 저장)
//        + 회원가입(100점)포함
		if (no != -1) {
			member.setUserImg(no);
			member.setUserPoint(howmuch);
			insertCnt = insertMember(member, con); // 1이면 insert성공
		}
		
//		(3) pointlog테이블에 회원가입 포인트 로그를 남겨야 함.
		int logCnt = -1;
		if (insertCnt == 1) {
			logCnt = insertPointLog(why, howmuch, member.getUserId(), con);
		}
		
		if (no != -1 && insertCnt == 1 && logCnt == 1 ) {
			con.commit();
			result = 0; // 트랜잭션 성공 0 반환
		} else {
			con.rollback();
		}
		
		con.setAutoCommit(true); // 트랜잭션 처리 끝
		con.close();
		return result;
	}

	@Override
	public int registerMember(Member member, String why, int howmuch) throws NamingException, SQLException {
		int result = -1;
		
		Connection con = DBConnection.getInstance().dbConnect();
		con.setAutoCommit(false);
		
		// --B. 업로드된 파일이 없는 경우
		// (1) 회원 가입 (순수한 회원 데이터 저장 + userImg에 default(1)이 저장) + 회원가입(100점)포함
		member.setUserPoint(howmuch);
		
		int logCnt = -1;
		int insertCnt = insertMember(member, con, false);
		
		if (insertCnt == 1 ) {
		// 회원가입 완료된  경우
		// (2) pointlog테이블에 회원가입 포인트 로그를 남겨야 함.
			logCnt = insertPointLog(why, howmuch, member.getUserId(), con);
		}
		
		if (insertCnt == 1 && logCnt == 1) {
			result = 0;
			con.commit();
		} else {
			con.rollback();
		}
		
		con.setAutoCommit(true);
		con.close();
		
		return result;
	}

	public int insertMember(Member member, Connection con, boolean userImg) throws SQLException {
		int result = -1;
		
		String query ="insert into member(userId, userPwd, userEmail, userPoint) values (?, sha1(md5(?)), ?, ?)";
		
		PreparedStatement pstmt = con.prepareStatement(query);
		
		pstmt.setString(1, member.getUserId());
		pstmt.setString(2, member.getUserPwd());
		pstmt.setString(3, member.getUserEmail());
		pstmt.setInt(4, member.getUserPoint());
		
		result = pstmt.executeUpdate();
		
		pstmt.close();
		
		return result;
	}
	@Override
	public int insertMember(Member newMember, Connection con) throws NamingException, SQLException {
		//(2) 회원 가입 (순수한 회원 데이터 저장 + (1)번에서 저장된 no (pk)를 userImg에 저장) 
		//+ 회원가입(100점)포함
		
		int result = -1;
		String query ="insert into member(userId, userPwd,userEmail, userImg, userPoint) values (?, sha1(md5(?)), ?, ?, ?)";
		
		PreparedStatement pstmt = con.prepareStatement(query);
		pstmt.setString(1, newMember.getUserId());
		pstmt.setString(2, newMember.getUserPwd());
		pstmt.setString(3, newMember.getUserEmail());
		pstmt.setInt(4, newMember.getUserImg());
		pstmt.setInt(5, newMember.getUserPoint());

		result = pstmt.executeUpdate();
		pstmt.close(); 
		return result;
	}

	
	@Override
	public int insertUploadedFileInfo(UploadedFile uf, Connection con) throws NamingException, SQLException {
		
		int result = -1;
		String query ="insert into uploadedfile(originalFileName, ext, newFileName, fileSize) values(?,?,?,?)";
		
		PreparedStatement pstmt = con.prepareStatement(query);
		pstmt.setString(1, uf.getOriginalFileName());
		pstmt.setString(2, uf.getExt());
		pstmt.setString(3, uf.getNewFileName());
		pstmt.setLong(4, uf.getSize());
		
		pstmt.executeUpdate();
		pstmt.close(); 
		
		result = getUploadedFileNo(con, uf);// 현재 업로드된 파일의 저장 번호(no)
		return result;
	}

	private int getUploadedFileNo(Connection con, UploadedFile uf) throws SQLException {
		 // 현재 업로드된 파일의 저장 번호(no)를 select해와서 반환 
		
		int no = -1;
		String query = "select no from uploadedfile where newFileName = ?";
		
		PreparedStatement pstmt = con.prepareStatement(query);
		
		pstmt.setString(1, uf.getNewFileName());
		
		ResultSet rs = pstmt.executeQuery();
		
		while(rs.next()) {
			no = rs.getInt("no");
		}
		
		rs.close(); 
		pstmt.close();
		return no;
	}


	@Override
	public int insertPointLog(String why, int howmuch, String userId, Connection con) throws NamingException, SQLException {
//		(3) pointlog테이블에 회원가입 포인트 로그를 남겨야 함.
		
		int result = -1;
		String query ="insert into pointlog(why, howmuch, who) values(?, ?, ?)";
		
		PreparedStatement pstmt = con.prepareStatement(query);
		pstmt.setString(1, why);
		pstmt.setInt(2, howmuch);
		pstmt.setString(3, userId);

		result = pstmt.executeUpdate();
		pstmt.close(); 
		
		return result;
	}

	@Override
	public Member loginMember(String userId, String userPwd) throws NamingException, SQLException {
		
		Member loginMember = null;
		
		Connection con = DBConnection.getInstance().dbConnect();
		
		
		String query = "select m.*, u.newFileName "
				+ "from member m inner join uploadedfile u "
				+ "on m.userImg = u.no "
				+ "where userId = ? and userPwd = sha1(md5(?))";
		
		
		PreparedStatement pstmt = con.prepareStatement(query);
		
		pstmt.setString(1, userId);
		pstmt.setString(2, userPwd);
		
		ResultSet rs = pstmt.executeQuery();
		
		while(rs.next()) {
			loginMember = new Member(rs.getString("userId"), 
					rs.getString("userPwd"), 
					rs.getString("userEmail"), 
					rs.getDate("registerDate"), 
					rs.getInt("userImg"), 
					rs.getInt("userPoint"), 
					rs.getString("newFileName"), 
					rs.getString("isAdmin"));
		}
		
		DBConnection.getInstance().dbClose(rs, pstmt, con);
		
		return loginMember;
	}

	@Override
	public int addPointToMember(String userId, String why, int howmuch) throws NamingException, SQLException {
		// 멤버 포인트 업데이트 + pointlog에 기록 남기기
		
		int result = -1;
		Connection con = DBConnection.getInstance().dbConnect();
		con.setAutoCommit(false);
		
		String query ="update member set userPoint = userPoint + ? where userId = ?";
		
		PreparedStatement pstmt = con.prepareStatement(query);
		pstmt.setInt(1, howmuch);
		pstmt.setString(2, userId);
		
		result = pstmt.executeUpdate();
		
		pstmt.close();
		
		if (result == 1) {
			// pointlog에 기록 남기기
			int afterPointLog =  insertPointLog(why, howmuch, userId, con);
			
			if (afterPointLog == 1) {
				con.commit();
				result = 0;
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
	
	public boolean addPointToMember(String userId, String why, int howmuch, Connection con) throws NamingException, SQLException {
		// 멤버 포인트 업데이트 + pointlog에 기록 남기기
		
		boolean resultPoint = false;
		
		String query ="update member set userPoint = userPoint + ? where userId = ?";
		
		PreparedStatement pstmt = con.prepareStatement(query);
		pstmt.setInt(1, howmuch);
		pstmt.setString(2, userId);
		
		int result = pstmt.executeUpdate();
		
		pstmt.close();
		
		if (result == 1) {
			// pointlog에 기록 남기기
			int afterPointLog =  insertPointLog(why, howmuch, userId, con);
			
			if (afterPointLog == 1) {
				con.commit();
				resultPoint = true;
			} else {
				con.rollback();
			}
			
		} else {
			con.rollback();
		}
		
		
		return resultPoint;
	}
	

	@Override
	public Member getMemberInfo(String userId) throws NamingException, SQLException {
		
		Member memberInfo = null;
		
		Connection con = DBConnection.getInstance().dbConnect();
		
		String query = "select m.*, u.newFileName "
				+ "from member m inner join uploadedfile u "
				+ "on m.userImg = u.no "
				+ "where userId = ?";
		
		PreparedStatement pstmt = con.prepareStatement(query);
		pstmt.setString(1, userId);
		
		ResultSet rs = pstmt.executeQuery();
		
		while(rs.next()) {
			memberInfo = new Member(rs.getString("userId"), 
					rs.getString("userPwd"), 
					rs.getString("userEmail"), 
					rs.getDate("registerDate"), 
					rs.getInt("userImg"), 
					rs.getInt("userPoint"),
					rs.getString("newFileName"), 
					rs.getString("isAdmin"));
		}
		
		DBConnection.getInstance().dbClose(rs, pstmt, con);
		
		return memberInfo;
	}

	@Override
	public List<PointLog> getPointLog(String userId) throws NamingException, SQLException {
		
		List<PointLog> pl = new ArrayList<PointLog>();
		
		Connection con = DBConnection.getInstance().dbConnect();
		
		String query ="select * from pointlog where who = ?";
		
		PreparedStatement pstmt = con.prepareStatement(query);
		pstmt.setString(1, userId);
		
		ResultSet rs = pstmt.executeQuery();
		while(rs.next()) {
			pl.add(new PointLog(rs.getInt("id"), 
					rs.getDate("when"), 
					rs.getString("why"), 
					rs.getInt("howmuch"), 
					rs.getString("who")));
		}
		
		DBConnection.getInstance().dbClose(rs, pstmt, con);
		
		return pl;
	}

}
