package findwatson.member.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import findwatson.member.dao.MemberDAO;
import findwatson.member.dto.MemberDTO;


@WebServlet("*.member")
public class memberController extends HttpServlet {
	private static final long serialVersionUID = 1L;


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		request.setCharacterEncoding("UTF-8");
		String URI = request.getRequestURI(); 
		String ctxpath = request.getContextPath(); 
		String path = URI.substring(ctxpath.length()); 
		System.out.println(path);

		MemberDAO dao = MemberDAO.getInstance();


		if(path.contentEquals("/login.member")) {
			String id = request.getParameter("id");
			String pw = request.getParameter("pw");
			System.out.println(id);
			System.out.println(pw);

			try {
				boolean result = dao.loginOk(id, pw);
				if(result) {
					request.getSession().setAttribute("loginInfo",id);
					response.sendRedirect("main.jsp");

				}else {
					response.sendRedirect("index.jsp");

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}//로그아웃
		else if (path.contentEquals("/logout.member")) {
			request.getSession().removeAttribute("loginInfo");
			response.sendRedirect("index.jsp");
		}//회원가입
		else if(path.contentEquals("/signUp.member")) {
			String id = request.getParameter("id");
			String pw = request.getParameter("pw");
			String name = request.getParameter("name");
			String birth = request.getParameter("birth");
			String gender = request.getParameter("gender");
			String email = request.getParameter("email");
			String phone = request.getParameter("phone");
			String postcode =request.getParameter("postcode");
			String address1 = request.getParameter("address1");
			String address2 = request.getParameter("address2");
			String lovePet = request.getParameter("lovePet");
			String signPath = request.getParameter("signPath");

			System.out.println(id);

			MemberDTO dto = new MemberDTO(id,pw,name,birth,gender,email,phone,postcode,address1,address2,lovePet,signPath,null);
			try {
				int signup = dao.insert(dto);
				if(signup >0) {
					response.sendRedirect("index.jsp");
				}else {
					response.sendRedirect("error.jsp");
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}//회원탈퇴
		else if(path.contentEquals("/mypageWithdrawal.member")) {
			String id = (String)request.getSession().getAttribute("loginInfo");
			System.out.println(id);
			try {
				int memberout = 0;
				memberout = dao.delete(id);
				if(memberout > 0) {
					request.getSession().invalidate();
					response.sendRedirect("index.jsp");
				}
			} catch (Exception e) {
				e.printStackTrace();
				response.sendRedirect("error.jsp");
			}

		}//마이페이지
		else if(path.contentEquals("/mypageInfo.member")) {
			String id = (String)request.getSession().getAttribute("loginInfo");
			try {
				MemberDTO dto = dao.selectMyInfo(id);
				request.setAttribute("dto", dto);
				request.getRequestDispatcher("member/mypageInfo.jsp").forward(request, response);
			} catch (Exception e) {
				e.printStackTrace();
				response.sendRedirect("error.jsp");
			}

		}//정보인포->정보수정으로 이동
		else if(path.contentEquals("/infoModify.member")) {
			String id = request.getParameter("id");
			String pw = request.getParameter("pw");
			String name = request.getParameter("name");
			String birth = request.getParameter("birth");
			String gender = request.getParameter("gender");
			String email = request.getParameter("email");
			String phone = request.getParameter("phone");
			String postcode =request.getParameter("postcode");
			String address1 = request.getParameter("address1");
			String address2 = request.getParameter("address2");
			String lovePet = request.getParameter("lovePet");
			String signPath = request.getParameter("signPath");
			//dto에 담아서 수정페이지로 보내기
			MemberDTO dto = new MemberDTO(id,pw,name,birth,gender,email,phone,postcode,address1,address2,lovePet,signPath,null);
			request.setAttribute("dto", dto);
			request.getRequestDispatcher("member/mypageModify.jsp").forward(request, response);
		}

	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		doGet(request, response);
	}

}

