package appathon.history.models;

import java.util.HashMap;

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
	
	private static HashMap<String, LatLng> llMap = initailzeLLMap();
	
	private String name; // 
	private int avatar; // Store R.drawable.flag
	private LatLng ll;
	
	public Country(String name) {
		super();
		this.name = name;
		this.ll = llMap.get(name);
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
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
	
	private static HashMap<String, LatLng> initailzeLLMap() {
		return new HashMap<String, LatLng>();
	}
	
}
