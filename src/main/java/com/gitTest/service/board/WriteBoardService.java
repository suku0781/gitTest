package com.gitTest.service.board;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;

import com.gitTest.controller.BoardFactory;
import com.gitTest.dao.BoardCRUD;
import com.gitTest.dao.BoardDAO;
import com.gitTest.etc.UploadedFile;
import com.gitTest.service.BoardService;
import com.gitTest.vo.Board;

public class WriteBoardService implements BoardService {

	private static final int MEMORY_THRESHOLD = 1024 * 1024 * 5;  // 하나의 파일블럭의 버퍼 사이즈 5MB
	private static final int MAX_FILE_SIZE = 1024 * 1024 * 10; // 최대 파일 사이즈 10MB
	private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 15; // 최대 request 사이즈 15MB
	
	
	@Override
	public BoardFactory doAction(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		System.out.println("게시글 저장하러 가자");
		
		BoardFactory bf = BoardFactory.getInstance();
		
		// 파일 업로드할 디렉토리를 생성
		String uploadDir = "\\uploads";
		
		String realPath = request.getSession().getServletContext().getRealPath(uploadDir);
		System.out.println("realPath: " + realPath);
		
		// File객체 만들기 
		File saveFileDir = new File(realPath);
		
		String writer = "";
		String title = "";
		String content ="";
		
		String encoding = "utf-8";
		
		// 파일이 저장될 공간의 경로, 사이즈 등의 환경설정 정보를 가지고 있는 객체
		DiskFileItemFactory factory = new DiskFileItemFactory(MEMORY_THRESHOLD, saveFileDir);
		
//		DiskFileItemFactory factory1 = new DiskFileItemFactory();
//		factory1.setSizeThreshold(MEMORY_THRESHOLD);
//		factory1.setRepository(saveFileDir);
		
		// 실제 request로 넘겨져온 매개변수를 통해 파일을 upload처리 할 객체
		ServletFileUpload sfu = new ServletFileUpload(factory);
		UploadedFile uf = null;
		
		try {
			List<FileItem> lst = sfu.parseRequest(request);
		
		
		// FileItem 속성에서 :
		// 1) name값이 null이 아니면 파일 (name값이 파일 이름 (확장자포함))
		// 2)  isFormField 의 값이 true이면 파일이 아닌 데이터
		//     isFormField 의 값이 false이면 파일
		// 3)  FieldName의 값이 보내온 데이터의 input태그의 name속성 값
		
		for (FileItem item : lst) {
			System.out.println(item.toString());
			
			if (item.isFormField()) { // 파일이 아닌 일반 데이터
				if (item.getFieldName().equals("writer")) {
					writer = item.getString(encoding);
				} else if (item.getFieldName().equals("title")) {
					title = item.getString(encoding);
				} else if (item.getFieldName().equals("content")) {
					content = item.getString(encoding);
				}
				
				
			} else if (item.isFormField() == false && item.getName() != "") { // 업로드된 파일인 경우
				// 파일이름 중복 제거
				// 1) 중복되지 않을 새이름으로 파일명을 변경 : uuid이용 
				// writer_uuid
					uf = getNewFileName(item, realPath, writer); 
				
					System.out.println(uf.toString());
					
				// 2) 오리지널파일명(순서번호).확장자
//					uf = makeNewFileNameWithNumbering(item, realPath);
				
				// 파일 하드디스크에 저장
					File fileToSave = new File(realPath + File.separator + uf.getNewFileName());
					
					try {
						item.write(fileToSave); // 파일 하드디스크에 저장
						
						uf.setBase64String(makeImgToBase64String(realPath + File.separator + uf.getNewFileName())); 
//						System.out.println(uf.toString());
						
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
			}
			
		
		}
		
	} catch (FileUploadException e) {
		// 파일이 업로드될 때의 예외
		e.printStackTrace();
	}
		
		System.out.println("writer: " + writer);
		System.out.println("title: " + title);
		System.out.println("content: " + content);
		
	// ------ 게시글 저장 진행
		// 본문에 줄바꿈이 있다면 줄바꿈처리를 해야 한다. \r\n -> <br>
//		content = content.replace("\r\n", "<br />");
		
		BoardDAO bdao = BoardCRUD.getInstance();
		int result = -1;	
		Board tmpBoard = new Board(-1, writer, title, null, content, -1, -1, -1, -1, -1, null);
		
		try {
			if (uf != null) { // 업로드된 파일이 있는 경우
				uf.setNewFileName("uploads/"+uf.getNewFileName());
				
				result = bdao.insertBoardWithUploadFileTransaction(tmpBoard, uf);
				System.out.println("업로드된 파일이 있는 경우 글저장 성공 result : " + result);
				
			} else { // 업로된 파일이 없는 경우
				result = bdao.insertBoardTransaction(tmpBoard);
				System.out.println("업로드된 파일이 없는 경우 글저장 성공 result : " + result);
			}
			
			if (result == 0) {
				System.out.println("게시글 저장 all 성공");
			}
			
		} catch (NamingException | SQLException e) {
			// DB에 저장할 때 나오는 예외
			e.printStackTrace();
			
			if (uf != null) {
				// 업로드된 파일이 있다면 삭제해야 함.
				System.out.println("삭제할 이미지: " + uf.getNewFileName());
				
				String without = uf.getNewFileName().substring("uploads/".length());
//				System.out.println(without);
				
				File deleteFile = new File(realPath + File.separator + without);
				deleteFile.delete(); // 파일 삭제
			}
		
			// 글저장시 예외발생 -> 어디로? listAll.bo로 이동시키자
				bf.setRedirect(true);
				bf.setWhereToGo("listAll.bo");
						
			return bf;
		}
	
		// 글저장 성공 후에는 어디로?? listAll.jsp로 보낸다. 
		bf.setRedirect(true);
		bf.setWhereToGo("listAll.bo");
		
		return bf;
	}


	private String makeImgToBase64String(String uploadedFile) {
		//image파일을 base64String로 만들기
	// 인코딩 (파일 -> 문자열) 
		String result = null;
		
		File upfile = new File(uploadedFile);
		
		byte[] file;
		try {
			file = FileUtils.readFileToByteArray(upfile);
			result = Base64.getEncoder().encodeToString(file);
			
//			System.out.println("base64인코딩 결과: " + result);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	// 디코딩 (base64문자열 -> 파일)
//		String encodedStr = result;
//		
//		byte[] decodedArr = Base64.getDecoder().decode(encodedStr);
//		String realPath = "D:\\lecture\\jsp\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\wtpwebapps\\JSPMiniProject\\uploads";
//		
//		File f = new File(realPath + File.separator + "aaaa.jpg");
//		try {
//			FileUtils.writeByteArrayToFile(f, decodedArr);
//			System.out.println("베이스64문자열을 파일로 저장완료");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		return result;
		
	}


	private UploadedFile getNewFileName(FileItem item, String realPath, String writer) {
		// userId_UUID로 새파일이름 만들기
		String uuid = UUID.randomUUID().toString();
		String originalFileName = item.getName(); // 업로드된 원본파일 이름 ex: rock.png
		String ext = originalFileName.substring(originalFileName.lastIndexOf(".")); // .png
		
	//	System.out.println("originalFileName : " + originalFileName + ", ext: " + ext); 
		
		String newFileName = "";
		
		if (item.getSize() > 0) { // 실제 파일이 저장되는 경우 고려
			newFileName += writer + "_" + uuid + ext;
		}
		
	//	System.out.println(newFileName);
		UploadedFile uf = new UploadedFile(originalFileName, ext, newFileName, item.getSize());
		
		return uf;
	}
}
