package services;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Geo extends Thread
{
	public static PrintStream Log = System.out;

	private Socket client;
	private Geo (Socket client) {this.client = client;}
	
	
	public static String cal (String request)
	{
		String[] input = request.split(" ");
		double[] in = new double[4];
		for (int i = 0; i < 4; i++){in[i] = Double.parseDouble(input[i]);}
		
		double t1 = (Math.PI/180) * in[0];
		double n1 = (Math.PI/180) * in[1];
		double t2 = (Math.PI/180) * in[2];
		double n2 = (Math.PI/180) * in[3];
		
		double Y = Math.cos(t1) * Math.cos(t2);
		double X = Math.pow(Math.sin( (t2-t1)/2) , 2) + Y * Math.pow(Math.sin( (n2-n1)/2) , 2);
		
		double out = 12742 * Math.atan2(Math.sqrt(X), Math.sqrt(1-X));
		return Double.toString(out);
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
					+ "[+-]?(\\d+)(.\\d+)?(\\ )"
					+ "[+-]?(\\d+)(.\\d+)?(\\ )"
					+ "[+-]?(\\d+)(.\\d+)?$"))
			{
				response = cal(request);
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
				
				(new Geo(client)).start();
			}
		}
	}

}
