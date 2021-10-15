package services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.*;
import java.sql.*;
import java.util.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import com.google.gson.Gson;

import services.models.*;

public class Quote extends Thread
{
	public static PrintStream Log = System.out;

	private Socket client;
	private Quote (Socket client) {this.client = client;}
	
	
	public static String findInDB(String in) throws Exception
	{
		String out = "";
		String url   = "jdbc:derby://localhost:64413/EECS";
	    
	    try (Connection connection = DriverManager.getConnection(url))
	    {
	    	Log.printf("Connected to database: %s\n", connection.getMetaData().getURL());
	        String query = "SELECT * FROM hr.product WHERE ID = ?";
	        
	        try(PreparedStatement statement = connection.prepareStatement(query))
	        {
	        	statement.setString(1, in);
	        	try (ResultSet rs = statement.executeQuery())
	        	{
	        		List<ProductBean> products = new ArrayList<>();
	        		ProductCollection collection = new ProductCollection();
	        		
	        		while (rs.next()) 
	        		{
	        			ProductBean bean = new ProductBean();
	        			
	        			bean.setID(rs.getString("ID"));
	        			bean.setName(rs.getString("NAME")); 
//	        			bean.setDescription(rs.getString("DESCRIPTION")); 
//	        			bean.setQty(rs.getInt("QTY"));
	        			bean.setCost(rs.getDouble("COST"));
//	        			bean.setMsrp(rs.getDouble("MSRP"));
//	        			bean.setCatID(rs.getInt("CATID"));
//	        			bean.setVenID(rs.getInt("VENID"));

	        			products.add(bean);
	        		}
	        		if (products.size() < 1) 
	        		{
	        			ProductBean bean = new ProductBean();
	        			
	        			bean.setID(in + " not found");
	        			bean.setName(""); 
//	        			bean.setDescription(rs.getString("DESCRIPTION")); 
//	        			bean.setQty(rs.getInt("QTY"));
	        			bean.setCost(0.0);
//	        			bean.setMsrp(rs.getDouble("MSRP"));
//	        			bean.setCatID(rs.getInt("CATID"));
//	        			bean.setVenID(rs.getInt("VENID"));

	        			products.add(bean);
	        		}
	        		collection.setProducts(products);
//	        		for(ProductBean bean : products) {Log.println(bean.toString());}
	        		
	        		//XML out
	        		try(ByteArrayOutputStream baos = new ByteArrayOutputStream())
	        		{
	        			JAXBContext context = JAXBContext.newInstance(ProductCollection.class);
	                    Marshaller m = context.createMarshaller();
	                    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
	                    m.marshal(collection, baos);
	                    
	                    out = baos.toString();
	                    
//	                    Log.println(baos);
	        		}

	        		//JSON out
	        		Gson gson = new Gson();
	        		out += "\n"+gson.toJson(collection);
//	                Log.println(gson.toJson(collection));
	        	}
	        } 
	        catch (Exception e) {Log.println(e);}
	    }
	    catch (Exception e) {Log.println(e);}
	    finally {Log.println("Disconnected from database.");}

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
			
			if (request.matches("^([A-Z]\\d+)_(\\d+)$"))
			{
				response = findInDB(request);
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
				
				(new Quote(client)).start();
			}
		}
	}

}
