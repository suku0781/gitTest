package com.jspminiproj.vo;

import java.sql.Date;

public class Member {
	private String userId;
	private String userPwd;
	private String userEmail;
	private Date registerDate;
	private int userImg;
	private int userPoint;
	private String memberImg;
	private String isAdmin;
	
	public Member(String userId, String userPwd, String userEmail, Date registerDate, int userImg, int userPoint) {
		super();
		this.userId = userId;
		this.userPwd = userPwd;
		this.userEmail = userEmail;
		this.registerDate = registerDate;
		this.userImg = userImg;
		this.userPoint = userPoint;
	}
	
	public Member(String userId, String userPwd, String userEmail, Date registerDate, int userImg, int userPoint, String memberImg, String isAdmin) {
		super();
		this.userId = userId;
		this.userPwd = userPwd;
		this.userEmail = userEmail;
		this.registerDate = registerDate;
		this.userImg = userImg;
		this.userPoint = userPoint;
		this.memberImg = memberImg;
		this.isAdmin = isAdmin;
	}

	
	
	
	public String getMemberImg() {
		return memberImg;
	}

	public void setMemberImg(String memberImg) {
		this.memberImg = memberImg;
	}

	public String getIsAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(String isAdmin) {
		this.isAdmin = isAdmin;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserPwd() {
		return userPwd;
	}

	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public Date getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}

	public int getUserImg() {
		return userImg;
	}

	public void setUserImg(int userImg) {
		this.userImg = userImg;
	}

	public int getUserPoint() {
		return userPoint;
	}

	public void setUserPoint(int userPoint) {
		this.userPoint = userPoint;
	}

	@Override
	public String toString() {
		return "Member [userId=" + userId + ", userPwd=" + userPwd + ", userEmail=" + userEmail + ", registerDate="
				+ registerDate + ", userImg=" + userImg + ", userPoint=" + userPoint + ", memberImg=" + memberImg
				+ ", isAdmin=" + isAdmin + "]";
	}

	
	
	
	
	
	
	
}
