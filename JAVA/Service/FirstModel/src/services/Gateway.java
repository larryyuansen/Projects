package services;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Gateway extends Thread
{
	public static PrintStream Log = System.out;

	private Socket client;
	private Gateway (Socket client) {this.client = client;}
	private static final Map<Integer, String> httpResponseCodes = new HashMap<>();
	static {
	    httpResponseCodes.put(100, "HTTP CONTINUE");
	    httpResponseCodes.put(101, "SWITCHING PROTOCOLS");
	    httpResponseCodes.put(200, "OK");
	    httpResponseCodes.put(201, "CREATED");
	    httpResponseCodes.put(202, "ACCEPTED");
	    httpResponseCodes.put(203, "NON AUTHORITATIVE INFORMATION");
	    httpResponseCodes.put(204, "NO CONTENT");
	    httpResponseCodes.put(205, "RESET CONTENT");
	    httpResponseCodes.put(206, "PARTIAL CONTENT");
	    httpResponseCodes.put(300, "MULTIPLE CHOICES");
	    httpResponseCodes.put(301, "MOVED PERMANENTLY");
	    httpResponseCodes.put(302, "MOVED TEMPORARILY");
	    httpResponseCodes.put(303, "SEE OTHER");
	    httpResponseCodes.put(304, "NOT MODIFIED");
	    httpResponseCodes.put(305, "USE PROXY");
	    httpResponseCodes.put(400, "BAD REQUEST");
	    httpResponseCodes.put(401, "UNAUTHORIZED");
	    httpResponseCodes.put(402, "PAYMENT REQUIRED");
	    httpResponseCodes.put(403, "FORBIDDEN");
	    httpResponseCodes.put(404, "NOT FOUND");
	    httpResponseCodes.put(405, "METHOD NOT ALLOWED");
	    httpResponseCodes.put(406, "NOT ACCEPTABLE");
	    httpResponseCodes.put(407, "PROXY AUTHENTICATION REQUIRED");
	    httpResponseCodes.put(408, "REQUEST TIME OUT");
	    httpResponseCodes.put(409, "CONFLICT");
	    httpResponseCodes.put(410, "GONE");
	    httpResponseCodes.put(411, "LENGTH REQUIRED");
	    httpResponseCodes.put(412, "PRECONDITION FAILED");
	    httpResponseCodes.put(413, "REQUEST ENTITY TOO LARGE");
	    httpResponseCodes.put(414, "REQUEST URI TOO LARGE");
	    httpResponseCodes.put(415, "UNSUPPORTED MEDIA TYPE");
	    httpResponseCodes.put(500, "INTERNAL SERVER ERROR");
	    httpResponseCodes.put(501, "NOT IMPLEMENTED");
	    httpResponseCodes.put(502, "BAD GATEWAY");
	    httpResponseCodes.put(503, "SERVICE UNAVAILABLE");
	    httpResponseCodes.put(504, "GATEWAY TIME OUT");
	    httpResponseCodes.put(505, "HTTP VERSION NOT SUPPORTED");
	  }

	
	private void sendHeaders(PrintStream res, int code, String contentType, String response) 
	{
	// send HTTP Headers
		res.printf("HTTP/1.1 %d %s\n", code, httpResponseCodes.get(code));
		res.println("Server: Java HTTP Server : 1.0");
		res.println("Date: " + new Date());
		res.println("Content-type: " + contentType);
		res.println("Content-length: " + response.getBytes().length);
		res.println(); // blank line between headers and content, very important !
	}
	
	public String connectServer (String request, int port) throws Exception
	{
		Socket serClient = new Socket(hostAdd, port);
		PrintStream req = new PrintStream(serClient.getOutputStream(), true);
		Scanner res1 = new Scanner(serClient.getInputStream());	
		
		Log.printf("Connected to Another Server %s:%d\n", serClient.getInetAddress(), serClient.getPort());
		
		req.println(request);
		String out = "";
		while (res1.hasNext())
		{
			out += res1.nextLine();
			out += "\n";
		}
		
		serClient.close();
		Log.printf("Disconnected from Another Server %s:%d\n", serClient.getInetAddress(), serClient.getPort());
		
		return out;
	}
	
	public void running(String request, PrintStream res) throws Exception
	{
		Scanner parse = new Scanner(request);
		String method = parse.next();
		String resource = parse.next();
		String version = parse.next();
		
		if (!method.equals("GET") && !method.equals("HEAD"))
		{
			this.sendHeaders(res, 501, "text/plain", this.httpResponseCodes.get(501));
		}
		else if (!version.equals("HTTP/1.1"))
		{
			this.sendHeaders(res, 505, "text/plain", this.httpResponseCodes.get(505));
		}
		else
		{
			String[] in = resource.split("\\?");
			if (in.length == 2)
			{
//				/SRV?p1=v1&p2=v2
				String out = "";

				in[0] = in[0].toUpperCase();
				String[] arg = in[1].split("\\&");
				if (in[0].equals("/GEO"))
				{
					if (arg.length == 4)
					{
						String reque ="";
						for (String str : arg)
						{
							String q1 = str.split("\\=")[1];
							reque += q1 + " ";
						}
						reque = reque.substring(0, reque.length()-1);
						out += this.connectServer(reque, portGeo);
					} 
				}
				else if (in[0].equals("/QUOTE"))
				{
					if (arg.length == 1)
					{
						String q1 = arg[0].split("\\=")[1];
						
						out += this.connectServer(q1, portQuote);
					} 
				}
				else if (in[0].equals("/AUTH"))
				{
					if (arg.length == 2)
					{
						String reque="";
						for (String str : arg)
						{
							String q1 = str.split("\\=")[1];
							reque += q1 + " ";
						}
						reque = reque.substring(0, reque.length()-1);
						out += this.connectServer(reque, portAuth);
					} 
				}
				

				this.sendHeaders(res, 200, "text/plain", out);
				res.println(out);
			}
			else
			{
				this.sendHeaders(res, 400, "text/plain", httpResponseCodes.get(400));
			}
		}
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
			String out = "";
			String request = req.nextLine().trim();
			
			this.running(request, res);
			
			res.println(out);
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
	
	
	static int portGeo=0, portQuote=0, portAuth=0;
	static InetAddress hostAdd;
	public static void main(String[] args) throws Exception
	{
		// TODO Auto-generated method stub
		int port = 0;
		InetAddress host = InetAddress.getLocalHost();
		//get input arg[0] for geo port, arg[1] for Quote port, arg[2] for Auth port
		portGeo = Integer.parseInt(args[0]);
		portQuote = Integer.parseInt(args[1]);
		portAuth = Integer.parseInt(args[2]);
		
		try (ServerSocket server = new ServerSocket(port, 0, host))
		{
			Log.printf("Server listening on %s:%d\n", server.getInetAddress(), server.getLocalPort());
			hostAdd = server.getInetAddress();
			while (true)
			{
				Socket client = server.accept();
						
				(new Gateway(client)).start();
			}
		}
	}

}
