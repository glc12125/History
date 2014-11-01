package appathon.history.models;

public class User implements Comparable<User>
{
	private String name;

	private boolean isAI;

	private int score;

	private String avatar;

	private boolean questionSubmitted;

	public String getName()
	{
		return name;
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

	public int getScore()
	{
		return score;
	}

	public void setScore(int score)
	{
		this.score = score;
	}

	public String getAvatar()
	{
		return avatar;
	}

	public void setAvatar(String avatar)
	{
		this.avatar = avatar;
	}

	public boolean isQuestionSubmitted()
	{
		return questionSubmitted;
	}

	public void setQuestionSubmitted(boolean questionSubmitted)
	{
		this.questionSubmitted = questionSubmitted;
	}

	public String sendResult(Question question, String selectedAnswer)
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

	public void restart()
	{
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
		return name + "         total scores:" + score;
	}

}
