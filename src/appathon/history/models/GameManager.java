package appathon.history.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;

public class GameManager {
	private QuestionGenerator questionGenerator;
	private Timer timer;
	private ArrayList<Question> questions;
	private HashMap<Country, ArrayList<Question>> countryToQuestionsMap;
	private HashMap<Country, User> countryToUser;
	private int currentQuestionIndex;
	
	public GameManager() {
		super();
		currentQuestionIndex = 0;
	}

	public QuestionGenerator getQuestionGenerator() {
		return questionGenerator;
	}
	
	public void setQuestionGenerator(QuestionGenerator questionGenerator) {
		this.questionGenerator = questionGenerator;
	}
	
	public Timer getTimer() {
		return timer;
	}
	
	public void setTimer(Timer timer) {
		this.timer = timer;
	}
	
	public ArrayList<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(ArrayList<Question> questions) {
		this.questions = questions;
	}
	
	public HashMap<Country, ArrayList<Question>> getCountryToQuestionsMap() {
		return countryToQuestionsMap;
	}
	
	public void setCountryToQuestionsMap(
			HashMap<Country, ArrayList<Question>> countryToQuestionsMap) {
		this.countryToQuestionsMap = countryToQuestionsMap;
	}
	
	public HashMap<Country, User> getCountryToUser() {
		return countryToUser;
	}
	
	public void setCountryToUser(HashMap<Country, User> countryToUser) {
		this.countryToUser = countryToUser;
	}
	
	public int getCurrentQuestion() {
		return currentQuestionIndex;
	}
	
	public boolean checkAnswers(ArrayList<User> users){
		
		int count = 0;
		for(int i = 0; i < users.size(); i++){
			users.get(i).isQuestionSubmitted();
			count++;
		}
		if(count == users.size()){
			return true;
		}
		
		return false;
	}
	
	public void RestartUsers(ArrayList<User> users){
		for(int i = 0; i < users.size(); ++i){
			users.get(i).restart();
		}
	}
	
	public Question NextQuestion(){
		if(currentQuestionIndex < questions.size()){
			currentQuestionIndex++;
			return questions.get(currentQuestionIndex);
		}
		return null;
	}
	
}
