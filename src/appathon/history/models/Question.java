package appathon.history.models;
import java.util.ArrayList;

public class Question {
	
	public String kind = "ChooseOption";  // ChooseCountry, ChooseCity, ChooseOption
	public String question = null;
	public String imgUrl = null;
	public ArrayList<Answer> options = new ArrayList<Answer>();
	public String correctAnswer = null;
	public Answer country = null;
	
	public Question(String question, String imgUrl, ArrayList<Answer> options,
			String correctAnswer, String country, String kind) {
		super();
		this.question = question;
		this.imgUrl = imgUrl;
		this.options = options;
		this.correctAnswer = correctAnswer;
		this.country = new Answer(country, true);
		this.kind = kind;
	}

	@Override
	public String toString() {
		return "[" + country + "] " + question;
	}
	
}
