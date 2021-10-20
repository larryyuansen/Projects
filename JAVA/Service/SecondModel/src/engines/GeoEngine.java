package engines;

import java.io.File;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class GeoEngine {
	private static GeoEngine engine = null;
	private static PrintStream Log = System.out;
	
	private GeoEngine() {}
	
	public static GeoEngine getInstance()
	{
		if (engine == null) engine = new GeoEngine();
		return engine;
	}
	
	public synchronized String getGeo (String request) 
	{
		String out = "";
	
		String Host	= "";
		int port	= 0;
		
		File serverLocator = new File("/tmp/" + server.Geo.class.getName());
	    try (Scanner in = new Scanner(serverLocator)) {
	      String[] idAddress = in.nextLine().split(":");
	      Host = idAddress[0];
	      port = Integer.parseInt(idAddress[1]);
	    } catch (Exception e) {
	      Log.println(e);
	      return "Failed to get Auth service's host and port addresses";
	    }
		
		//Try Auth
		try 
		(		
			Socket geoClient = new Socket(Host, port);
			PrintStream req = new PrintStream(geoClient.getOutputStream(), true);
			Scanner res = new Scanner(geoClient.getInputStream());	
		)
		{
			Log.printf("Connected to Auth %s:%d\n", 
					geoClient.getInetAddress(), geoClient.getPort());
			req.println(request);
			out += res.nextLine();
			geoClient .close();
			Log.printf("Disconnected from Auth %s:%d\n", 
					geoClient.getInetAddress(), geoClient.getPort());
		}
		catch (Exception e)
		{
			Log.println(e);
			return "Fail to complete connectino with username and password";
		}

		return out;
	}
}
