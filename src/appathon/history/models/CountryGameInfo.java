package appathon.history.models;

import java.util.ArrayList;

import appathon.history.models.qa.Question;

import com.google.android.gms.maps.model.Marker;

public class CountryGameInfo {
	private Country country;
	private int defense;
	private User user;
	private Marker marker;
	private ArrayList<Question> questionList;
	
	public CountryGameInfo(Country country) {
		this.country = country;
		defense = 0;
		questionList = new ArrayList<Question>();
	}
	
	public CountryGameInfo(Country country, Question q) {
		this(country);
		addQuestion(q);
	}
	
	
	public void addQuestion(Question q) {
		questionList.add(q);
	}
	
	public Country getCountry() {
		return country;
	}
	public void setCountry(Country country) {
		this.country = country;
	}
	public int getDefense() {
		return defense;
	}
	public void setDefense(int defense) {
		this.defense = defense;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Marker getMarker() {
		return marker;
	}
	public void setMarker(Marker marker) {
		this.marker = marker;
	}
	
	
}
