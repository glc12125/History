package appathon.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

public class LocationGetter
{
	
	private static LocationGetter lg;
	
	private Context context;
	private HashMap<String, LatLng> llMap; 
	
	
	public static LocationGetter getLocationGetter() {
		if(lg == null) throw new NullPointerException("LocationGetter is not intailized with context");
		return lg;
	}
	
	public static LocationGetter getLocationGetter(Context context) {
		if(lg == null) lg = new LocationGetter(context);
		return lg;
	}
	
	private LocationGetter(Context context) {
		this.context = context;
		initailzeLLMap();
	}
	
	public String getCountryName(Context context, double latitude,
			double longitude)
	{
		Geocoder geocoder = new Geocoder(context, Locale.getDefault());
		List<Address> addresses = null;
		try
		{
			addresses = geocoder.getFromLocation(latitude, longitude, 1);
		} catch (IOException ignored)
		{
		}
		if (addresses != null && !addresses.isEmpty())
		{
			return addresses.get(0).getCountryName();
		}
		return null;
	}

	public LatLng getLocationFromAddress(Context context, String strAddress){
		return getLocationFromAddress(context, strAddress, 0.0);
	}
	
	public LatLng getLocationFromAddress(Context context, String strAddress, double latDiff)
	{
		if(llMap.containsKey(strAddress)) return llMap.get(strAddress);
		
		Geocoder coder = new Geocoder(context, Locale.getDefault());
		List<Address> address;
		LatLng ll = null;

		try
		{
			address = coder.getFromLocationName(strAddress, 5);
			if (address == null)
			{
				Log.e("getLocationFromAddress", strAddress);
				return null;
			}
			Address location = address.get(0);

			ll = new LatLng(location.getLatitude() + latDiff,location.getLongitude() );
		} catch (IOException e)
		{
		}
		llMap.put(strAddress, ll);
		return ll;
	}
	
	public void initailzeLLMap() {
		try {
			InputStream is = context.getAssets().open("geocoding.json");
			BufferedReader reader = new BufferedReader((new InputStreamReader(is, "UTF-8")));	
			String line = reader.readLine();
			Gson gson = new Gson();
			Wrapper response = gson.fromJson(line, Wrapper.class);
			llMap = response.getMap(); 
	    } catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IndexOutOfBoundsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private class Wrapper {
		
		public HashMap<String, LatLng> getMap() {
			HashMap<String, LatLng> map = new HashMap<String, LatLng>();
			for (LatLngResponse llr: countries) {
				map.put(llr.getCountryName(), new LatLng(llr.getLat(), llr.getLng()));
			}
			return map;
		}
		
		public List<LatLngResponse> countries;
		public void setCountries(List<LatLngResponse> lst) {
			countries = lst;
		}
		public List<LatLngResponse> getCountries() {
			return countries;
		}
	}
	
	private class LatLngResponse {
		public String countryName;
		public double lat;
		public double lng;
		public String getCountryName() {
			return countryName;
		}
		public void setCountryName(String countryName) {
			this.countryName = countryName;
		}
		public double getLat() {
			return lat;
		}
		public void setLat(double lat) {
			this.lat = lat;
		}
		public double getLng() {
			return lng;
		}
		public void setLng(double lng) {
			this.lng = lng;
		}
	}
}
