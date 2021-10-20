package services;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class Service
 */
@WebServlet("/FAuth")
public class FAuth extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FAuth() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		response.getWriter().append("Served at: ").append(request.getContextPath());
		
		engines.AuthEngine engine = engines.AuthEngine.getInstance();
		Writer out = response.getWriter();
		@SuppressWarnings("unused")
		HttpSession session = request.getSession(true);
		
		//set response type
		response.setContentType("text/plain");
		
		//Session
//		if (session.getAttribute("name") == null)
//			session.setAttribute("name", "");
//		String contact_name = (String) session.getAttribute("name");
		
		Map<String, String[]> parms = request.getParameterMap();
		String outText ="";
		
		//given username and password
		if (parms.containsKey("username")&&parms.containsKey("password"))
		{
			String uname	= request.getParameter("username");
			String psw		= request.getParameter("password");
//			outText += "user name: " + uname + " password: " + psw;
			String back = engine.getAuth(uname+" "+psw);
		//any back check?
			outText += back;
		}
		else
		{
			outText += "parameter error"; 
		}
		
		out.write(outText);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
