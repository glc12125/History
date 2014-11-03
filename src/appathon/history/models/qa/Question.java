package appathon.history.models.qa;
import java.util.ArrayList;

import appathon.history.models.Country;

public class Question {
	
	public String kind;  // ChooseCountry, ChooseCity, ChooseOption
	public String question;
	public String imgUrl;
	public ArrayList<Answer> options;
	public String correctAnswer;
	public Country correspondingCountry;
	
	public Question(String question, String imgUrl, ArrayList<Answer> options,
			String correctAnswer, String countryName, String kind) {
		super();
		this.question = question;
		this.imgUrl = imgUrl;
		this.options = options;
		this.correctAnswer = correctAnswer;
		this.correspondingCountry = new Country(countryName);
		this.kind = kind;
	}

	@Override
	public String toString() {
		return "[" + correspondingCountry + "] " + question;
	}
	
}
