package appathon.history.models;

import java.util.ArrayList;
import java.util.HashMap;

public class GameManager {
	private QuestionGenerator questionGenerator;
	private ArrayList<Question> questions;
	private ArrayList<User> users;
	private HashMap<Country, ArrayList<Question>> countryToQuestionsMap;
	private HashMap<Country, User> countryToUser;
	private int currentQuestionIndex;
	
	public GameManager() {
		super();
		currentQuestionIndex = 0;
		User user = new User("CG500", false, 1);
		users.add(user);
		questions = questionGenerator.getQuestions(10);
		
		for(Question question : questions){
			Country country = new Country(question.country.answer);
			if(countryToQuestionsMap.containsKey(country)){
				countryToQuestionsMap.get(country).add(question);
			}
			else{
				ArrayList<Question> newList= new ArrayList<Question>();
				newList.add(question);
				countryToQuestionsMap.put(country, newList);
			}
		}
	}

	public QuestionGenerator getQuestionGenerator() {
		return questionGenerator;
	}
	
	public void setQuestionGenerator(QuestionGenerator questionGenerator) {
		this.questionGenerator = questionGenerator;
	}
	
	public ArrayList<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(ArrayList<Question> questions) {
		this.questions = questions;
	}
	
	public ArrayList<User> getUsers() {
		return users;
	}

	public void setUsers(ArrayList<User> users) {
		this.users = users;
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
	
	public int getCurrentQuestionIndex() {
		return currentQuestionIndex;
	}
	
	public boolean checkAnswers(){
		
		int count = 0;
		for(int i = 0; i < users.size(); i++){
			if(users.get(i).isQuestionSubmitted()){
				count++;
				if(users.get(i).getSelectedAnswer() == questions.get(this.currentQuestionIndex).correctAnswer){
					return true;
				}
			}
		}
		if(count == users.size()){
			return true;
		}
		else{
			return false;
		}
		
	}
	
	public void RestartUsers(){
		for(int i = 0; i < users.size(); ++i){
			users.get(i).restart();
		}
	}
	
	public Question NextQuestion(){
		if(currentQuestionIndex < questions.size()){
			currentQuestionIndex++;
			RestartUsers();
			return questions.get(currentQuestionIndex);
		}
		return null;
	}
	
	public boolean updateUser(String userName, String selectedAnswer){

		for(User user : users){
			if(user.getName() == userName){
				user.setSelectedAnswer(selectedAnswer);
				return true;
			}
		}
		
		return false;
	}
	
}
