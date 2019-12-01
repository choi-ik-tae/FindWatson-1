package findwatson.admin.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

import findwatson.admin.dao.AdminDAO;
import findwatson.admin.dao.AdminFileDAO;
import findwatson.admin.dto.AdminFileDTO;
import findwatson.admin.dto.BanDTO;
import findwatson.admin.dto.ExpertDTO;
import findwatson.admin.dto.HListDTO;
import findwatson.admin.dto.NoticeDTO;
import findwatson.board.dao.BoardDAO;
import findwatson.board.dao.ObODAO;
import findwatson.board.dto.BoardDTO;
import findwatson.board.dto.ObODTO;
import findwatson.configuration.Configuration;
import findwatson.member.dto.MemberDTO;

@WebServlet("*.admin")
public class AdminController extends HttpServlet {
	private static final long serialVersionUID = 1L;
  
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		Scanner sc = new Scanner(System.in);
		
		String ipAddr = request.getRemoteAddr();
		String requestURI = request.getRequestURI();
		String contextPath = request.getContextPath();
		String cmd = requestURI.substring(contextPath.length());
		//String id = (String)request.getSession().getAttribute("loginInfo");
		String id = "test";
		
		AdminDAO dao = AdminDAO.getInstance();
		ObODAO Odao = ObODAO.getInstance();
		AdminFileDAO fDao = AdminFileDAO.getInstance();
		PrintWriter pwriter = response.getWriter();
		
		System.out.println("cmd - " + cmd);
		try {
			//관리자 로그인
			if(cmd.contentEquals("관리자 로그인")) {
				String idInput = request.getParameter("id");
				String pwInput = request.getParameter("pw");
				boolean result = dao.adminLogin(idInput, pwInput);
				
				if(result) {
					request.getSession().setAttribute("id", idInput);
					request.setAttribute("result", result);
					//request.getRequestDispatcher("관리자 로그인 후 페이지").forward(request, response);
				}
				else {
					response.sendRedirect("관리자 로그인 실패 페이지");
				}
			}else if(cmd.contentEquals("관리자 비밀번호 변경")) {//관리자 비밀번호 변경
				String pw = request.getParameter("pw");
				int result = dao.adminInfoPwUpdate(id, pw);
				if(result > 0) {
					response.sendRedirect("index.jsp");
				}
				
			}else if(cmd.contentEquals("/admin/adminMemberList.admin")) {//회원전체목록
				//List<MemberDTO> list = dao.selectAll();
				//request.setAttribute("list", list);
				System.out.println("admincontroller 연결 성공");
				//네비
				int cpage = 1;
				String param = request.getParameter("cpage");
				
				if(param!=null) {
					cpage = Integer.parseInt(param);	
				}
				
				int start = cpage * Configuration.recordCountPerPage - (Configuration.recordCountPerPage-1);	
				int end = cpage * Configuration.recordCountPerPage;
				
				List<MemberDTO> list = dao.MemberListByPage(start, end);
				
				String pageNavi = dao.getMemberListPageNav(cpage);
				
				request.setAttribute("pageNavi", pageNavi);
				request.setAttribute("list", list);
				
				//
				
				request.getRequestDispatcher("/admin/adminMemberList.jsp").forward(request, response);
				
			}else if(cmd.contentEquals("/admin/adminBanList.admin")) {//차단한 ip 목록
				
				//List<BanDTO> list = dao.selectBanList();
				//request.setAttribute("list", list);
				int cpage = 1;
				String param = request.getParameter("cpage");
				
				if(param!=null) {
					cpage = Integer.parseInt(param);	
				}
				
				int start = cpage * Configuration.recordCountPerPage - (Configuration.recordCountPerPage-1);	
				int end = cpage * Configuration.recordCountPerPage;
				
				List<BanDTO> list = dao.BanListByPage(start, end);
				
				String pageNavi = dao.getBanListPageNav(cpage);
				
				request.setAttribute("pageNavi", pageNavi);
				request.setAttribute("list", list);
				
				//
				request.getRequestDispatcher("/admin/adminBanList.jsp").forward(request, response);
			}else if(cmd.contentEquals("아이디로 회원 검색")) {//회원목록에서 아이디로 회원 검색
				List<MemberDTO> list = dao.selectById(id);
				request.setAttribute("list", list);
				
			}else if(cmd.contentEquals("/expertWrite.admin")){ //전문가 Q&A 글쓰기
				String title = request.getParameter("boardTitle");
				String content = request.getParameter("content");
				System.out.println(content);
				
				dao.insertToExpert(new ExpertDTO(0, id, title, content, null, 0));
				
				response.sendRedirect(contextPath + "/boardExpert.admin");
			}else if(cmd.contentEquals("/expertWriteImgUpload.admin")) {//전문가 Q&A 글쓰기-이미지 업로드
				String repositoryName = "expertImg";
				String uploadPath = request.getServletContext().getRealPath("/" + repositoryName);
				System.out.println(uploadPath);
				File uploadFilePath = new File(uploadPath);
				if(!uploadFilePath.exists()) {
					uploadFilePath.mkdir();
				}
				
				int maxSize = 1024 * 1024 * 100;
				MultipartRequest multi = new MultipartRequest(request,uploadPath, maxSize,"UTF-8",new DefaultFileRenamePolicy());
				
				String fileName = multi.getFilesystemName("expertImg");
				String oriFileName = multi.getOriginalFileName("expertImg");
				System.out.println("원래 파일 이름 : " + oriFileName);
				System.out.println("올린 파일 이름 : " + fileName);
				
				fDao.insertImgToExpert(new AdminFileDTO(0, 0, fileName, oriFileName));
				
				//서버의 이미지 경로
				String imgPath = "../" + repositoryName + "/" + fileName;
				System.out.println(imgPath);
				
				JsonObject jObj = new JsonObject();
				jObj.addProperty("imgPath", imgPath);
				pwriter.append(jObj.toString());
			}else if(cmd.contentEquals("/noticeWrite.admin")) {//공지사항 글쓰기
				String title = request.getParameter("boardTitle");
				String content = request.getParameter("content");
				System.out.println(content);
				
				dao.insertToNotice(new ExpertDTO(0, id, title, content, null, 0));
				
				response.sendRedirect(contextPath + "/boardNotice.admin");
			}else if(cmd.contentEquals("/noticeWriteImgUpload.admin")) {//공지사항 글쓰기 - 이미지 업로드
				String repositoryName = "noticeImg";
				String uploadPath = request.getServletContext().getRealPath("/" + repositoryName);
				System.out.println(uploadPath);
				File uploadFilePath = new File(uploadPath);
				if(!uploadFilePath.exists()) {
					uploadFilePath.mkdir();
				}
				int maxSize = 1024 * 1024 * 100;
				MultipartRequest multi = new MultipartRequest(request,uploadPath, maxSize,"UTF-8",new DefaultFileRenamePolicy());
				
				String fileName = multi.getFilesystemName("noticeImg");
				String oriFileName = multi.getOriginalFileName("noticeImg");
				System.out.println("원래 파일 이름 : " + oriFileName);
				System.out.println("올린 파일 이름 : " + fileName);
				
				fDao.insertImgToNotice(new AdminFileDTO(0, 0, fileName, oriFileName));
				
				//서버의 이미지 경로
				String imgPath = "../" + repositoryName + "/" + fileName;
				System.out.println(imgPath);
				
				JsonObject jObj = new JsonObject();
				jObj.addProperty("imgPath", imgPath);
				pwriter.append(jObj.toString());
			}else if(cmd.contentEquals("/boardExpert.admin")){// 전문가Q&A 글 목록 출력
				String pageCategory = "boardExpert.bo";
				int cpage = 1;
				String page = request.getParameter("cpage");
				if(page != null) {
					cpage = Integer.parseInt(request.getParameter("cpage"));
				}
				int start = cpage * Configuration.recordCountPerPage - Configuration.recordCountPerPage - 1;
				int end = cpage * Configuration.recordCountPerPage;
				List<ExpertDTO> list = BoardDAO.getInstance().selectByPageExpert(start, end);
				String pageNavi = BoardDAO.getInstance().getPageNavi(cpage,pageCategory);
				request.setAttribute("list", list);
				request.setAttribute("pageNavi", pageNavi);
				request.getRequestDispatcher("admin/adminBoardExpert.jsp").forward(request, response);
				
			}else if(cmd.contentEquals("/boardNotice.admin")){// 공지사항 글 목록 출력
				String pageCategory = "boardNotice.bo";
				int cpage = 1;
				String page = request.getParameter("cpage");
				if(page != null) {
					cpage = Integer.parseInt(request.getParameter("cpage"));
				}
				int start = cpage * Configuration.recordCountPerPage - Configuration.recordCountPerPage - 1;
				int end = cpage * Configuration.recordCountPerPage;
				
				List<NoticeDTO> list = BoardDAO.getInstance().selectByPageNotice(start, end);
				String pageNavi =  BoardDAO.getInstance().getPageNavi(cpage,pageCategory);
				
				request.setAttribute("list", list);
				request.setAttribute("pageNavi", pageNavi);
				request.getRequestDispatcher("admin/adminBoardNotice.jsp").forward(request, response);
			
			}else if(cmd.contentEquals("/boardFree.admin")){//자유게시판 글 출력
				String pageCategory = "boardFree.bo";
				int cpage = 1;
				String page = request.getParameter("cpage");
				if(page != null) {
					cpage = Integer.parseInt(request.getParameter("cpage"));
				}
				int start = cpage * Configuration.recordCountPerPage - Configuration.recordCountPerPage - 1;
				int end = cpage * Configuration.recordCountPerPage;
				
				List<BoardDTO> list = BoardDAO.getInstance().selectByPage(start, end, "자유");
				String pageNavi = BoardDAO.getInstance().getPageNavi(cpage,pageCategory);
				
				request.setAttribute("list", list);
				request.setAttribute("pageNavi", pageNavi);
				request.getRequestDispatcher("admin/adminBoardFree.jsp").forward(request, response);
			}else if(cmd.contentEquals("/boardQuestion.admin")) { //질문게시판 글 출력
				String pageCategory = "boardQuestion.bo";
				int cpage = 1;
				String page = request.getParameter("cpage");
				if(page != null) {
					cpage = Integer.parseInt(request.getParameter("cpage"));
				}
				int start = cpage * Configuration.recordCountPerPage - Configuration.recordCountPerPage - 1;
				int end = cpage * Configuration.recordCountPerPage;
				
				List<BoardDTO> list = BoardDAO.getInstance().selectByPage(start, end, "질문");
				String pageNavi = BoardDAO.getInstance().getPageNavi(cpage,pageCategory);
				
				request.setAttribute("list", list);
				request.setAttribute("pageNavi", pageNavi);
				request.getRequestDispatcher("admin/adminBoardQuestion.jsp").forward(request, response);
			}else if(cmd.contentEquals("/admin/adminDeleteMember.admin")) {//회원 삭제
				String idInput = request.getParameter("id");
				int result = dao.deleteMember(idInput);
				if(result > 0) {
					System.out.println("아이디 삭제 성공");
					response.sendRedirect("adminMemberList.admin");
				}
			}
			else if(cmd.contentEquals("/admin/adminSearchMember.admin")) {//회원목록에서 회원아이디 검색
				System.out.println("회원아이디검색 진입성공");
				String idInput = request.getParameter("search");
				List<MemberDTO> list = dao.selectById(idInput);
				request.setAttribute("list", list);
				request.getRequestDispatcher("/admin/adminMemberList.jsp").forward(request, response);
				
			// 병원 정보 입력
			} else if(cmd.contentEquals("/hosptInfoInsert.admin")) {
				String repositoryName = "hospitalImg";
				String uploadPath = request.getServletContext().getRealPath("/" + repositoryName);
				
				File uploadFilePath = new File(uploadPath);
				if(!uploadFilePath.exists()) {
					uploadFilePath.mkdir();
				}

				int maxSize = 1024 * 1024 * 10; // 10MB 용량 제한
				MultipartRequest multi = new MultipartRequest(request, uploadPath, maxSize, "UTF8", new DefaultFileRenamePolicy());
				
				String name = multi.getParameter("name");
				int postcode = Integer.parseInt(multi.getParameter("postcode"));
				String address1 = multi.getParameter("address1");
				String address2 = multi.getParameter("address2");
				String phone = multi.getParameter("phone");
				String homepage = multi.getParameter("homepage");
				String[] medicalAnimalArr = multi.getParameterValues("medicalAnimal");
				String[] openTimeArr = multi.getParameterValues("openTime");
				String image = multi.getFilesystemName("image");
				
				String medicalAnimal = Arrays.toString(medicalAnimalArr).replace("{","").replace("}","").replace("[","").replace("]","").replace(", ",";");
				if(medicalAnimal.contentEquals("null")) {
					medicalAnimal = "";
				}
				System.out.println("animal : " + medicalAnimal);
				
				String openTime = Arrays.toString(openTimeArr).replace("{","").replace("}","").replace("[","").replace("]","").replace(", ",";");
				if(openTime.contentEquals("null")) {
					openTime = "";
				}
				System.out.println("animal : " + openTime);
				
				//System.out.println(uploadPath);
				//System.out.println(name+"/"+postcode+"/"+address1+"/"+address2+"/"+phone+"/"+homepage+"/"+medicalAnimal.length+"/"+openTime.length+"/"+image);
				
				HListDTO Hdto = new HListDTO(0,name,postcode,address1,address2,phone,homepage,image,medicalAnimal,openTime,null,0);
				int result = dao.insertHospitalInfo(Hdto);
				if(result > 0) {
					System.out.println("병원 정보 입력 성공");
					response.sendRedirect("adminMemberList.admin");
				}
			// 1:1 문의 게시글 출력
			} else if(cmd.contentEquals("/adminOneByOne.admin")) {
				int cpage = 1;
				String page = request.getParameter("cpage");
				if(page != null) {
					cpage = Integer.parseInt(request.getParameter("cpage"));
				}
				int start = cpage * Configuration.recordCountPerPage - Configuration.recordCountPerPage - 1;
				int end = cpage * Configuration.recordCountPerPage;
				
				List<ObODTO> list = Odao.ObOByPage(start, end);
				String pageNavi = Odao.getObOPageNav(cpage);
				
				request.setAttribute("list", list);
				request.setAttribute("pageNavi", pageNavi);
				request.getRequestDispatcher("admin/adminBoardObO.jsp").forward(request, response);
			} else if(cmd.contentEquals("")) {
					
			} else if(cmd.contentEquals("")) {
				
			} else {
				response.sendRedirect(contextPath + "/error.jsp");
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			response.sendRedirect(contextPath + "/error.jsp");
		}
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
