package appathon.history.models;

public class User {
    private String name;
    
    private boolean isAI;

	private int countryNum;
    
    private String avatar;
    
    private boolean questionSubmitted;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    public boolean isAI() {
		return isAI;
	}

	public void setAI(boolean isAI) {
		this.isAI = isAI;
	}
	
	public int getCountryNum() {
		return countryNum;
	}

	public void setCountryNum(int countryNum) {
		this.countryNum = countryNum;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public boolean isQuestionSubmitted() {
		return questionSubmitted;
	}

	public void setQuestionSubmitted(boolean questionSubmitted) {
		this.questionSubmitted = questionSubmitted;
	}
    
    public String sendResult(Question question, String selectedAnswer){
    	if(isAI){
    		//randomly return answer
    	}
    	else{
    		return selectedAnswer;
    	}
    }
    
    public void restart(){
    	questionSubmitted = false;
    }
    
}
