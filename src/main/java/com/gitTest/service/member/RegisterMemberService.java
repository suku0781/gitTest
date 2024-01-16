package com.gitTest.service.member;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
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

import com.gitTest.controller.MemberFactory;
import com.gitTest.dao.MemberCRUD;
import com.gitTest.dao.MemberDAO;
import com.gitTest.etc.UploadedFile;
import com.gitTest.service.MemberService;
import com.gitTest.vo.Member;

public class RegisterMemberService implements MemberService {

	private static final int MEMORY_THRESHOLD = 1024 * 1024 * 5;  // 하나의 파일블럭의 버퍼 사이즈 5MB
	private static final int MAX_FILE_SIZE = 1024 * 1024 * 10; // 최대 파일 사이즈 10MB
	private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 15; // 최대 request 사이즈 15MB
	
	@Override
	public MemberFactory executeService(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("회원가입하러 가자~");
		
		MemberFactory mf = MemberFactory.getInstance();
		// 파일과 함께 데이터를 받았다면, request.getParameter()로 데이터를 수집하면 안된다(!!)
//		System.out.println(request.getParameter("userId"));
		
		
		// 파일 업로드할 디렉토리를 생성
		String uploadDir = "\\memberImg";
		// 실제 파일이 저장될 물리적 경로 
		String realPath = request.getSession().getServletContext().getRealPath(uploadDir);
		System.out.println("realPath: " + realPath);
		
		// File객체 만들기 
		File saveFileDir = new File(realPath);
		
		String userId = "";
		String userPwd ="";
		String email ="";
		String userImg ="";
		
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
					if (item.getFieldName().equals("userId")) {
						userId = item.getString(encoding);
					} else if (item.getFieldName().equals("userPwd")) {
						userPwd = item.getString(encoding);
					} else if (item.getFieldName().equals("userEmail")) {
						email = item.getString(encoding);
					}
					
					System.out.println("userId, userPwd, email : " + userId + ", " + userPwd + ", " + email);
					
				} else if (item.isFormField() == false && item.getName() != "") { // 업로드된 파일인 경우
					// 파일이름 중복 제거
					// 1) 중복되지 않을 새이름으로 파일명을 변경 : uuid이용 
					// userId_uuid
						uf = getNewFileName(item, realPath, userId); 
					
//						System.out.println(uf.toString());
						
					// 2) 오리지널파일명(순서번호).확장자
//						uf = makeNewFileNameWithNumbering(item, realPath);
					
					// 파일 하드디스크에 저장
						File fileToSave = new File(realPath + File.separator + uf.getNewFileName());
						
						try {
							item.write(fileToSave); // 파일 하드디스크에 저장
							
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
		
		// ------ 회원가입 진행 -----
		MemberDAO mdao = MemberCRUD.getInstance();
		int result = -1;
		
		try {
			if (uf != null) { // 업로드된 파일이 있는 경우
				uf.setNewFileName("memberImg/"+uf.getNewFileName());
				
				result = mdao.registerMemberWithFile(uf, new Member(userId, userPwd, email, null, -1, -1), "회원가입", 100);
				System.out.println("업로드된 파일이 있는 경우 회원가입 성공 result : " + result);
				
			} else { // 업로된 파일이 없는 경우
				result = mdao.registerMember(new Member(userId, userPwd, email, null, -1, -1), "회원가입", 100);
			}
			
			if (result == 0) {
				System.out.println("회원가입 all 성공");
			}
			
			
		} catch (NamingException | SQLException e) {
			// DB에 저장할 때 나오는 예외
			e.printStackTrace();
			
			if (uf != null) {
				// 업로드된 파일이 있다면 삭제해야 함.
				System.out.println("삭제할 이미지: " + uf.getNewFileName());
				// memberImg/aaaa_c6645aaf-131b-4f83-b527-4e97a2dab0dc.png
				// realPath = D:\lecture\jsp\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\JSPMiniProject\memberImg
				
				String without = uf.getNewFileName().substring("memberImg/".length());
//				System.out.println(without);
				
				File deleteFile = new File(realPath + File.separator + without);
				deleteFile.delete(); // 파일 삭제
				
			}
			
			// 회원가입시 예외발생 -> 어디로? 회원가입 페이지로 이동시키자
			mf.setRedirect(true);
			mf.setWhereToGo(request.getContextPath() + "/member/register.jsp?status=fail");
			
			return mf;
		}
		
		
		// 회원가입 성공 후에는 어디로?? index.jsp로 보낸다. 
		mf.setRedirect(true);
		mf.setWhereToGo(request.getContextPath() + "/index.jsp?status=success");
		
		
		return mf;
	}

	private UploadedFile makeNewFileNameWithNumbering(FileItem item, String realPath) {
		// ex) 파일명(번호).확장자 -> 새파일 이름을 만들기
		int cnt = 0;
		String tmpFileName = item.getName(); // 업로드된 원본이름
		String newFileName = ""; // 실제 저장되는 새파일명
		String ext = tmpFileName.substring(tmpFileName.lastIndexOf(".")); // .png
		
		while(duplicateFileName(tmpFileName, realPath)) { // 파일이 중복되면
			// 새파일이름 만들기
			cnt++;
			tmpFileName = makeNewFileName(tmpFileName, cnt);
		}
		
		newFileName = tmpFileName;
		
		UploadedFile uf = new UploadedFile(item.getName(), ext, newFileName, item.getSize());
		
		return uf;
	}

	private String makeNewFileName(String tmpFileName, int cnt) {
		//ex) 파일명(번호).확장자
		// rock.png -> rock + (1) + .png 
		// rock(1).png -> rock(2).png
		
		String newFileName = "";
		String ext = tmpFileName.substring(tmpFileName.lastIndexOf(".")); // .png
		String oldFileNameWithoutExt = tmpFileName.substring(0, tmpFileName.lastIndexOf(".")); 
		
		int openPos = oldFileNameWithoutExt.indexOf("(");
		
		if (openPos == -1) { // "(" 가 없다면 -> 처음 중복
			newFileName = oldFileNameWithoutExt + "(" + cnt + ")" + "_cp" + ext;
		} else {
			newFileName = oldFileNameWithoutExt.substring(0, openPos) + "(" + cnt + ")" + "_cp" + ext;
		}
		
		return newFileName;
			
	}

	private boolean duplicateFileName(String tmpFileName, String realPath) {
		boolean result = false;
		File tmpFileNamePath = new File(realPath);
		File[] files = tmpFileNamePath.listFiles(); // 파일리스트
		
//		System.out.println(Arrays.toString(files));
		
//		for (File f : files) {
//			if (f.getName().equals(tmpFileName)) {
//				System.out.println(tmpFileName + "이 중복됩니다..");
//				result = true;
//			}
//		}
		
		File tmpFile = new File(realPath + File.separator + tmpFileName);
		if (tmpFile.exists()) {
			System.out.println(tmpFileName + "이 중복됩니다..");
			result = true;
		}
		
		return result;
	}

	private UploadedFile getNewFileName(FileItem item, String realPath, String userId) {
		// userId_UUID로 새파일이름 만들기
		String uuid = UUID.randomUUID().toString();
		String originalFileName = item.getName(); // 업로드된 원본파일 이름 ex: rock.png
		String ext = originalFileName.substring(originalFileName.lastIndexOf(".")); // .png
		
//		System.out.println("originalFileName : " + originalFileName + ", ext: " + ext); 
		
		String newFileName = "";
		
		if (item.getSize() > 0) { // 실제 파일이 저장되는 경우 고려
			newFileName += userId + "_" + uuid + ext;
		}
		
//		System.out.println(newFileName);
		UploadedFile uf = new UploadedFile(originalFileName, ext, newFileName, item.getSize());
		
		return uf;
	}

}
