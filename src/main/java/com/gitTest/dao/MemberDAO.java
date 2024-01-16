package com.gitTest.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.naming.NamingException;

import com.gitTest.etc.UploadedFile;
import com.gitTest.vo.Member;
import com.gitTest.vo.PointLog;

public interface MemberDAO {
	// 유저아이디가 중복되는지 검사
 	Member duplicateUserId(String tmpUserId) throws NamingException, SQLException;
 	
 	
 	// 업로드된 파일이 있는 경우 회원가입
	int registerMemberWithFile(UploadedFile uf, Member member, String why, int howmuch) throws NamingException, SQLException;
	
	// 업로드된 파일이 없는 경우 회원가입
	int registerMember(Member member, String why, int howmuch) throws NamingException, SQLException;
	
	
	// 업로드된 파일의 정보를 uploadedFile테이블에 insert
	int insertUploadedFileInfo(UploadedFile uf, Connection con) throws NamingException, SQLException;
	
	// 회원정보 insert
	int insertMember(Member newMember, Connection con) throws NamingException, SQLException;
	
	// pointlog테이블에 회원가입 포인트 로그를 남김
	int insertPointLog(String why, int howmuch, String userId, Connection con) throws NamingException, SQLException;


	//	로그인 
	Member loginMember(String userId, String userPwd) throws NamingException, SQLException;

	// member 포인트 업데이트
	int addPointToMember(String userId, String why, int howmuch) throws NamingException, SQLException;
	
	
	

	// 해당 아이디 멤버 정보 가져오기
	Member getMemberInfo(String userId) throws NamingException, SQLException;

	// 해당 멤버의 포인트 기록 가져오기
	List<PointLog> getPointLog(String userId)  throws NamingException, SQLException; 
	
	

 	
}
