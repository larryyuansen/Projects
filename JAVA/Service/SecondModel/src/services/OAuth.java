package services;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class OAuth
 */
@WebServlet("/OAuth")
public class OAuth extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public OAuth() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		response.getWriter().append("Served at: ").append(request.getContextPath());
		Writer out = response.getWriter(); 
		
		//set response type
		response.setContentType("text/plain");
		System.out.println("Got");
		
		//incoming request
		Map<String, String[]> parms = request.getParameterMap();
		String outText ="";
		if (parms.containsKey("user")&&parms.containsKey("name")&&parms.containsKey("hash"))
		{
			
			String user = request.getParameter("user");
			String name = request.getParameter("name");
//			String hash = request.getParameter("hash");
			
//			outText += String.format(" user: %s, \n name: %s, \n hash: %s", user,name,hash);
			outText += String.format("Hello, %s. You are logged in as %s.", name,user);
		}
		else 
		{
			System.out.println("Got1");
			String cgi = "https://www.eecs.yorku.ca/~roumani/servers/auth/oauth.cgi";
			cgi += "?back=http://localhost:4413/B/OAuth";
			response.sendRedirect(cgi);
//			outText += "Back info not useful";
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
