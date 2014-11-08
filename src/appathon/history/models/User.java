package appathon.history.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import appathon.exception.NumOfCountriesException;
import appathon.history.R;
import appathon.history.models.qa.Question;

public class User implements Comparable<User>
{
	protected static ArrayList<Integer> availableIcons = new ArrayList<Integer>(Arrays.asList(
			R.drawable.zuck, R.drawable.ma, R.drawable.jobs, R.drawable.cameron,
			R.drawable.obama, R.drawable.merkel, R.drawable.kat));
	protected static ArrayList<String> availableNames = new ArrayList<String>(Arrays.asList(
			"Mårk Zückerberg", "Jäck Mā", "Stéve Jöbs", "Dävid Cămeron",
			"Baräck Øbama", "Angēla Mérkel", "Kam Kat"));
	protected static Random randomGenerator = new Random();
	
	private String name;
	private boolean isAI;
	private int id;
	private int score;
	private int ranking;
	private int avatar;
	private boolean questionSubmitted;
	private int small_avatar;
	private int numOfCountries;
	private boolean isChecked;
	protected long reactiveMillis;
	protected String selectedAnswer;

	public User(int id, String name, boolean isAI)
	{
		this.id = id;
		if(name != ""){
			this.name = name;
			this.small_avatar = R.drawable.player;
		}
		else{
			int nameIndex = User.randomGenerator.nextInt(User.availableNames.size());
			this.name = User.availableNames.get(nameIndex);
			this.small_avatar = User.availableIcons.get(nameIndex);
			User.availableNames.remove(nameIndex);
			User.availableIcons.remove(nameIndex);
		}
		this.isAI = isAI;
		this.score = 0;
		this.avatar = R.drawable.avatar_meng_zhang;
		this.questionSubmitted = false;
		this.numOfCountries = 0;
		this.reactiveMillis = -1;
		this.isChecked = false;
	}

	public long getReactiveMillis() {
		return this.reactiveMillis;
	}
	
	public void setReactiveMillis(long ms) {
		this.reactiveMillis = ms;
	}
	
	public void incrementNumOfCountriesByOne() {
		this.numOfCountries++;
	}
	
	public void decreaseNumOfCountriesByOne() throws NumOfCountriesException {
		if(numOfCountries == 0) 
			throw new NumOfCountriesException("The number of countries of one user should bigger than zero");
		this.numOfCountries--;
	}
	
	public int getNumOfQuestions() {
		return this.numOfCountries;
	}
	
	public String getName()
	{
		return name;
	}

	public int getSmallAvatar()
	{
		return this.small_avatar;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public boolean isAI()
	{
		return isAI;
	}

	public void setAI(boolean isAI)
	{
		this.isAI = isAI;
	}

	public boolean isChecked() {
		return isChecked;
	}
	
	public void checked() {
		isChecked = true;
	}
	
	public int getScore()
	{
		return score;
	}

	public void setScore(int score)
	{
		this.score = score;
	}

	public int getRanking()
	{
		return ranking;
	}

	public void setRanking(int ranking)
	{
		this.ranking = ranking;
	}

	public int getAvatar()
	{
		return avatar;
	}
	
	public int getId(){
		return id;
	}

	public boolean isQuestionSubmitted()
	{
		return questionSubmitted;
	}

	public void setQuestionSubmitted(boolean questionSubmitted)
	{
		this.questionSubmitted = questionSubmitted;
	}

	public String getSelectedAnswer()
	{
		return this.selectedAnswer;
	}

	public void setSelectedAnswer(String selectedAnswer)
	{
		this.selectedAnswer = selectedAnswer;
	}

	public String sendResult(Question question, String selectedAnswer)
	// Make an answer to one question
	{
		if (isAI)
		{
			// randomly return answer
			return null;
		} else
		{
			return selectedAnswer;
		}
	}

	public void restartQuestionSubmittedStatus()
	{
		isChecked = false;	
		questionSubmitted = false;
	}

	@Override
	public int compareTo(User aUser)
	{
		if (aUser instanceof User)
		{
			return (score - aUser.score);
		}

		throw new ClassCastException("Cannot compare User with "
				+ aUser.getClass().getName());
	}

	@Override
	public String toString()
	{
		return name + "\ttotal scores:" + score;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (name == null)
		{
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
