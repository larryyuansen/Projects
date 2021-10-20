package engines;

import java.io.File;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class AuthEngine {
	
	private static AuthEngine engine = null;
	private static PrintStream Log = System.out;
	
	private AuthEngine() {}
	
	public static AuthEngine getInstance()
	{
		if (engine == null) engine = new AuthEngine();
		return engine;
	}
	
	public synchronized String getAuth (String request) 
	{
		String out = "";
	
		String Host	= "";
		int port	= 0;
		
		File serverLocator = new File("/tmp/" + server.Auth.class.getName());
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
			Socket authClient = new Socket(Host, port);
			PrintStream req = new PrintStream(authClient.getOutputStream(), true);
			Scanner res = new Scanner(authClient.getInputStream());	
		)
		{
			Log.printf("Connected to Auth %s:%d\n", 
					authClient.getInetAddress(), authClient.getPort());
			req.println(request);
			out += res.nextLine();
			authClient .close();
			Log.printf("Disconnected from Auth %s:%d\n", 
					authClient.getInetAddress(), authClient.getPort());
		}
		catch (Exception e)
		{
			Log.println(e);
			return "Fail to complete connectino with username and password";
		}

		return out;
	}
}
