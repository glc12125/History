package appathon.util;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

public class LocationGetter
{

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
		return ll;
	}
}
