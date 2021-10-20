package server;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.Scanner;

public class Auth extends Thread
{
	public static PrintStream Log = System.out;

	private Socket client;
	private Auth (Socket client) {this.client = client;}
	
	
	public static String auth (String in) throws Exception
	{
		String out = "";
		String[] words = in.split(" ");
		if (words.length != 2)
		{
			out = "FAILURE: with wrong input " + in;
			return out;
		}
		
		String username = words[0];
		String password = words[1];

		
		//get from data base
		String home = System.getProperty("user.home");
	    String url  = "jdbc:sqlite:" + home + "/4413/pkg/sqlite/Models_R_US.db";
	    //connect
	    Connection connection = DriverManager.getConnection(url);
	    //
	    Log.printf("Connected to database: %s\n", connection.getMetaData().getURL());
	    String query = "SELECT * FROM client WHERE name = ?";

	    //send request
	    PreparedStatement statement = connection.prepareStatement(query);
	    statement.setString(1, username);
	    //get response
	    ResultSet rs = statement.executeQuery();
	    
	    String salt = "1";
	    int count = -1;
	    String hash = "";
	    String calHash = "1";
	    while (rs.next())
	    {
	    	salt = rs.getString("salt").trim();
	    	count = rs.getInt("count");
	    	hash = rs.getString("hash").trim();
	    	
			calHash = g.Util.hash(password, salt, count);
//			out += "name: " + name + " salt: "+salt +" count: " + count + " hash: \n"+hash+ " calHash: \n"+calHash;
	    }
	    
		if (calHash == "1") {out += "FAILURE: user not found: " + username;}
		else if (calHash.equals(hash)) {out += "OK";}
		else {out += "FAILURE";}

		return out;
	}
	
	public void run()
	{
		Log.printf("Connected to %s:%d\n", client.getInetAddress(), client.getPort());
		
		try
		(
				Socket  _client = this.client; // Makes sure that client is closed at end of try-statement. 
			    Scanner     req = new Scanner(client.getInputStream());
			    PrintStream res = new PrintStream(client.getOutputStream(), true);
		)
		{
			String response = "";
			String request = req.nextLine().trim();
			
			if (request.matches("^(\\w+)@(\\w+).(\\w+)(\\ )(\\w+)$"))
			{
				response = auth(request);
			}
			else		
			{
				response = "Don't understand: " + request;
			}
	
			res.println(response);
		}
		catch (Exception e)
		{
			Log.println(e);
		}
		finally
		{
			Log.printf("Disconnected from %s:%d\n", client.getInetAddress(), client.getPort());
			try {
				client.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.println("Fail to close Client Socket "+e.getMessage());
			}
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		// TODO Auto-generated method stub
		int port = 0;
		InetAddress host = InetAddress.getLocalHost();
	    
		//write to File
		File serverLocator = new File("/tmp/" + Auth.class.getName());

	    serverLocator.deleteOnExit();
	    if (!serverLocator.exists()) {
	      serverLocator.createNewFile();
	    }
				
		try (ServerSocket server = new ServerSocket(port, 0, host))
		{
			Log.printf("Server listening on %s:%d\n", server.getInetAddress(), server.getLocalPort());

			try (PrintStream out = new PrintStream(serverLocator, "UTF-8"))
			{
				out.printf("%s:%d\n", server.getInetAddress().getHostAddress(), server.getLocalPort());
		    }
			
			while (true)
			{
				Socket client = server.accept();
						
				(new Auth(client)).start();
			}
		}
	}

}
