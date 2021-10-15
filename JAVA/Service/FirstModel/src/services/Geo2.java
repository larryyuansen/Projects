package services;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Scanner;

public class Geo2 extends Thread
{
	public static PrintStream Log = System.out;

	private Socket client;
	private String in0; private int in1;
	private Geo2 (Socket client, String in0, int in1) 
	{
		this.client = client; 
		this.in0 = in0; this.in1 = in1;
	}
	
	private static HashMap<Integer,String > data = new HashMap<Integer, String>();;

	@SuppressWarnings("resource")
	public synchronized String geo2 (String request) throws Exception 
	{
		String out = "";
		
		String[] input = request.split(" ");
		if (input.length == 2) 
		{
			//new
			int key = (int) (Math.random()*1000);
			while(data.containsKey(key)) {key = (int) (Math.random()*1000);}
			data.put(key, request);
			out += key;
			
//			out += data.keySet().toString();
		} 
		else if (input.length == 3) 
		{

			int key = Integer.parseInt(input[2]);
			if (!data.keySet().contains(key))
			{
				out += "cookie wrong!";
//				out += "\n"+data.get(key)+"\n";
				return out;
			}
			// prepare request
			String second = input[0] + " " + input[1];
			String first = data.get(key);
			data.remove(key);
			// in is input for Geo
			String in = first + " " + second;
			
			
			//Try Geo
			Socket geoClient = new Socket(in0, in1);
			PrintStream req = new PrintStream(geoClient.getOutputStream(), true);
			Scanner res = new Scanner(geoClient.getInputStream());	
			Log.printf("Connected to Geo %s:%d\n", geoClient.getInetAddress(), geoClient.getPort());
			req.println(in);
			out += res.nextLine();
			geoClient.close();
			Log.printf("Disconnected from Geo %s:%d\n", geoClient.getInetAddress(), geoClient.getPort());
		}

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
			
			if (request.matches("^"
					+ "[+-]?(\\d+)(.\\d+)?(\\ )"
					+ "[+-]?(\\d+)(.\\d+)?(\\ )?"
					+ "[+-]?(\\d+)?$"))
			{
				response += geo2(request);
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
				
		try (ServerSocket server = new ServerSocket(port, 0, host))
		{
			Log.printf("Server listening on %s:%d\n", server.getInetAddress(), server.getLocalPort());
					
			while (true)
			{
				Socket client = server.accept();
				(new Geo2(client, args[0], Integer.parseInt(args[1]))).start();
			}
		}
	}

}
