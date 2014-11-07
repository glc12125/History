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
			R.drawable.astrologer, R.drawable.baby11, R.drawable.baby126,
			R.drawable.bicyclist, R.drawable.bookkeeper, R.drawable.business61,
			R.drawable.cool4, R.drawable.criminal, R.drawable.dude1,
			R.drawable.electrical, R.drawable.genius1, R.drawable.graduate6,
			R.drawable.karate2, R.drawable.magician3, R.drawable.motorcyclist1,
			R.drawable.palace, R.drawable.pilot, R.drawable.scientist,
			R.drawable.speaker48, R.drawable.spy1, R.drawable.stockbroker));
	protected static ArrayList<String> availableNames = new ArrayList<String>(Arrays.asList(
			"James", "John", "Robert", "Michael", "William", "David", "Richard", "Charles",
			"Joseph", "Thomas", "Mark", "Mary", "Patricia", "Linda", "Barbara", "Elizabeth",
			"Jennifer", "Maria", "Susan", "Margaret", "Dorothy", "Lisa", "Helen", "George"));
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
			User.availableNames.remove(name);
		}
		else{
			int nameIndex = User.randomGenerator.nextInt(User.availableNames.size());
			this.name = User.availableNames.get(nameIndex);
			User.availableNames.remove(nameIndex);
		}
		this.isAI = isAI;
		this.score = 0;
		this.avatar = R.drawable.avatar_meng_zhang;
		this.questionSubmitted = false;
		this.numOfCountries = 0;
		this.reactiveMillis = -1;
		this.isChecked = false;
		int iconIndex = User.randomGenerator.nextInt(User.availableIcons.size());
		this.small_avatar = User.availableIcons.get(iconIndex);
		User.availableIcons.remove(iconIndex);
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
