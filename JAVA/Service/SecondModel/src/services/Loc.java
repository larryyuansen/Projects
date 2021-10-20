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
 * Servlet implementation class Loc
 */
@WebServlet("/Loc")
public class Loc extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Loc() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Writer out = response.getWriter(); 
		@SuppressWarnings("unused")
		HttpSession session = request.getSession(true);
		
		//set response type
		response.setContentType("text/plain");
				
		Map<String, String[]> parms = request.getParameterMap();
		String outText ="";
		
		//given location
		if (parms.containsKey("location"))
		{
			String location	= request.getParameter("location");
			
			String back = engines.MapQuestAPI.getLocationLanLng(location);

			response.setContentType("application/json");

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
