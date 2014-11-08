package appathon.history.models;

import java.io.InputStream;
import java.util.HashMap;

import appathon.history.MainActivity;
import appathon.history.R;
import appathon.util.LocationGetter;

import com.google.android.gms.maps.model.LatLng;


/**
 * 
 * 
 * It is the entity which maps to Google Map
 * It should be a real country exists in Google Map 
 * like Russia, but not Soviet Union 
 * 
 * All Game info like defense, the user who controls this country 
 * should not go in to this class
 * @author mengzhang
 *
 */
public class Country {
	
	private static HashMap<String, String> countryNameMap = initailzeCountryNameMap();
	private static HashMap<String, Integer> countryFlagMap = initializeCountryFlagMap();
	private String name; // 
	private int flag;
	private LatLng ll;
	
	public Country(String name) {
		super();
		this.name = name;
		this.flag = countryFlagMap.get(name);
		this.ll = LocationGetter.getLocationGetter().getLocationFromAddress(name);
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public LatLng getLatLng() {
		return ll;
	}
	
	public int getFlag(){
		return flag;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Country other = (Country) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	

	private static HashMap<String, String> initailzeCountryNameMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		return map;
	}
	
	private static HashMap<String, Integer> initializeCountryFlagMap(){
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("Bosnia and Herzegovina", R.drawable.ba);
		map.put("China", R.drawable.cn);
		map.put("Cuba", R.drawable.cu);
		map.put("Germany", R.drawable.de);
		map.put("Denmark", R.drawable.dk);
		map.put("Spain", R.drawable.es);
		map.put("Finland", R.drawable.fi);
		map.put("France", R.drawable.fr);
		map.put("UK", R.drawable.gb);
		map.put("Ireland", R.drawable.ie);
		map.put("Iran", R.drawable.ir);
		map.put("Japan", R.drawable.jp);
		map.put("Mexico", R.drawable.mx);
		map.put("Poland", R.drawable.pl);
		map.put("Palestine", R.drawable.ps);
		map.put("Russia", R.drawable.ru);
		map.put("Sweden", R.drawable.se);
		map.put("Turkey", R.drawable.tr);
		map.put("US", R.drawable.us);
		map.put("Uruguay", R.drawable.uy);
		return map;
	}
	
}
