package appathon.history.models;
import java.util.ArrayList;

public class Question {
	
	public String question = null;
	public String imgUrl = null;
	public ArrayList<Answer> options = new ArrayList<Answer>();
	public int answerKey = -1;
	public Answer country = null;
	
	public String kind(){
		if(this.options.isEmpty()){
			return "ChooseCountry";
		}
		else{
			if(this.options.get(0).isGeographic){
				return "ChooseCity";
			}
			else{
				return "ChooseOption";
			}
		}
	}
	
	public boolean isAnswerCorrect(int answerKey){
		return answerKey == this.answerKey;
	}
	
	public boolean isAnswerCorrect(String country){
		return this.country != null && country == this.country.answer;
	}

	public Question(String question, String imgUrl, ArrayList<Answer> options,
			int answerKey) {
		super();
		this.question = question;
		this.imgUrl = imgUrl;
		this.options = options;
		this.answerKey = answerKey;
	}
	
}
