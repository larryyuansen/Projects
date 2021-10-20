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
 * Servlet implementation class GeoWeb
 */
@WebServlet("/GeoWeb")
public class GeoWeb extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GeoWeb() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		response.getWriter().append("Served at: ").append(request.getContextPath());
	
		engines.GeoEngine engine = engines.GeoEngine.getInstance();
		Writer out = response.getWriter();
		HttpSession session = request.getSession(true);
		
		//set response type
		response.setContentType("text/plain");
		String outText ="";
		
		//get parameters
		Map<String, String[]> parms = request.getParameterMap();
		
		//Session
		if (session.getAttribute("location") == null)
		{
			session.setAttribute("location", "");
			
			if (parms.containsKey("lat")&&parms.containsKey("lng"))
			{
				String lat	= request.getParameter("lat");
				String lng	= request.getParameter("lng");

				session.setAttribute("location", lat+" "+lng);
				
				outText += "RECEIVED";
			}
			else
			{
				outText += "parameter error"; 
			}
		}
		else
		{
			String contact_latlng = (String) session.getAttribute("location");
			
			if (parms.containsKey("lat")&&parms.containsKey("lng"))
			{
				String lat1	= request.getParameter("lat");
				String lng1	= request.getParameter("lng");
				String[] latlng = contact_latlng.split(" ");
				System.out.println(latlng[0]);

				String back = engine.getGeo(latlng[0]+" "+latlng[1]
						+" "+lat1+" "+lng1);
				
				if (back.startsWith("Don't understand:"))
				{
					outText += "input error, start with begining, session cleared.";
					session.setAttribute("location", null);
				}
				else if (back.startsWith("Failed to get Auth service"))
				{
					outText += "running Geo seraver not found.";
					session.setAttribute("location", null);
				}
				else
				{
					outText += String.format("The distance from (%s, %s) to (%s, %s) is: %s km",
							latlng[0],latlng[1],lat1,lng1,back);
					
					session.setAttribute("location", null);
				}

			}
			else
			{
				outText += "parameter error"; 
			}
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
