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
 * Servlet implementation class Drone
 */
@WebServlet("/Drone")
public class Drone extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Drone() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		engines.GeoEngine engine = engines.GeoEngine.getInstance();
		Writer out = response.getWriter(); 
		@SuppressWarnings("unused")
		HttpSession session = request.getSession(true);
		
		//set response type
		response.setContentType("text/plain");
	
		Map<String, String[]> parms = request.getParameterMap();
		String outText ="";
		
		//given location
		if (parms.containsKey("source") && parms.containsKey("destination"))
		{
			String source		= request.getParameter("source");
			String destination	= request.getParameter("destination");
			
			String back1 = engines.MapQuestAPI.getLocationLanLng(source);
			back1 = engines.MapQuestAPI.JsonToString(back1);
			String back2 = engines.MapQuestAPI.getLocationLanLng(destination);
			back2 = engines.MapQuestAPI.JsonToString(back2);

			String back	= engine.getGeo(back1+" "+back2);
			if (back.startsWith("Don't understand"))
				outText = "Wrong input: \n" + back;
			else if (back.startsWith("Failed to "))
				outText = "Geo Server not found: \n" + back;
			else 
			{
				double time = 60 * (Double.parseDouble(back) / 150);
				outText = String.format("The estimated delivery time is: %s minutes.", Double.toString(time));
			}
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
