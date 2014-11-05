package appathon.history.models;

import java.util.ArrayList;

import appathon.exception.NumOfCountriesException;
import appathon.history.R;
import appathon.history.models.qa.Question;

public class User implements Comparable<User>
{
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
	private String selectedAnswer;

	public User(int id, String name, boolean isAI, int avatar, int small_avatar)
	{
		this.id = id;
		this.name = name;
		this.isAI = isAI;
		this.score = 0;
		this.avatar = avatar;
		this.questionSubmitted = false;
		this.numOfCountries = 0;
		this.small_avatar = small_avatar;
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
		return selectedAnswer;
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

	public ArrayList<User> generateFakeUsers()
	{
		ArrayList<User> user_list = new ArrayList<User>();
		user_list.add(new User(1, "Meng Zhang", true,
				R.drawable.avatar_meng_zhang,
				R.drawable.avatar_meng_zhang_small));
		user_list.add(new User(2, "Chao Gao", true, R.drawable.avatar_chao_gao,
				R.drawable.avatar_chao_gao_small));
		user_list.add(new User(3, "Liangchuan Gu", true,
				R.drawable.avatar_liangchuan_gu,
				R.drawable.avatar_liang_chuan_small));
		user_list.add(new User(4, "Yimai Fang", false,
				R.drawable.avatar_yimai_fang, R.drawable.avatar_yi_mai_small));

		user_list.get(0).setScore(53);
		user_list.get(1).setScore(14);
		user_list.get(2).setScore(22);
		user_list.get(3).setScore(31);

		return user_list;
	}
}
