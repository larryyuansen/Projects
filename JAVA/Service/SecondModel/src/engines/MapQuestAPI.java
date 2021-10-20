package engines;

import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

import com.google.gson.*;

public class MapQuestAPI {
	
	private static final String KEY = "HGfApu5btuTQ9LIv2m6rAqGhVbNfN2hV";
	
	public static String getLocationLanLng(String location)
	{
		
		try
		{
			String out = "";
			String quest = String.format(
					"http://www.mapquestapi.com/geocoding/v1/address?key=%s&location=%s", 
					KEY, URLEncoder.encode(location, "UTF-8"));
			URL url = new URL(quest);
			
			@SuppressWarnings("resource")
			Scanner in = new Scanner((url).openStream());
			while(in.hasNextLine())
				out += in.nextLine() + "\n";
			String result = "";
			
			JsonElement jelement = new JsonParser().parse(out);
			JsonObject  jobject = jelement.getAsJsonObject();
			JsonArray jarray = jobject.getAsJsonArray("results");
			if (jarray.size()>0)
			{
				jobject = jarray.get(0).getAsJsonObject();
				jarray = jobject.getAsJsonArray("locations");
				if (jarray.size() > 0)
				{
					jobject = jarray.get(0).getAsJsonObject();
					result = jobject.get("latLng").toString();
				} else {result = "{ \"lat\": null, \"lng\": null }";}
			} else {result = "{ \"lat\": null, \"lng\": null }";}
			
			
			return result;
		}catch (Exception e)
		{
			System.out.println("Exception: " + e);
		}
		finally
		{
			
		}
		
		return "";
	}
	
	public static String JsonToString (String js)
	{
		JsonElement jelement = new JsonParser().parse(js);
		JsonObject  jobject = jelement.getAsJsonObject();
		String result = jobject.get("lat") +" "+jobject.get("lng");
		return result;
	}

}
