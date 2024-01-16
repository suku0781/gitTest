package com.gitTest.controller;

import com.gitTest.service.BoardService;
import com.gitTest.service.board.DeleteBoardService;
import com.gitTest.service.board.GetBoardByNoService;
import com.gitTest.service.board.GetEntireBoardService;
import com.gitTest.service.board.ReplyBoardService;
import com.gitTest.service.board.WrtieBoardService;

public class BoardFactory {
	private boolean isRedirect; //  redicrect를 할 것인지 말것인지 
	private String whereToGo;  // 어느 view단으로 이동할지
	
	private static BoardFactory instance = null;
	
	private BoardFactory() {}
	
	public static BoardFactory getInstance() {
		if (instance == null) {
			instance = new BoardFactory();
		}
		return instance;
	}

	
	public boolean isRedirect() {
		return isRedirect;
	}

	public void setRedirect(boolean isRedirect) {
		this.isRedirect = isRedirect;
	}

	public String getWhereToGo() {
		return whereToGo;
	}

	public void setWhereToGo(String whereToGo) {
		this.whereToGo = whereToGo;
	}
	
	public BoardService getService(String command) {
		
		BoardService service = null;
		
		if (command.equals("/board/listAll.bo")) {
			service = new GetEntireBoardService();
		} else if (command.equals("/board/writeBoard.bo")) {
			service = new WrtieBoardService();
		} else if (command.equals("/board/viewBoard.bo")) {
			service = new GetBoardByNoService();
		} else if (command.equals("/board/delBoard.bo")) {
			service = new DeleteBoardService();
		} else if (command.equals("/board/reply.bo")) {
			service = new ReplyBoardService();
		}
		
		
		return service;
		
	}
	
	
}
